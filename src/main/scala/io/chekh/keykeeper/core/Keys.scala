package io.chekh.keykeeper.core

import cats.implicits._
import io.chekh.keykeeper.logging.syntax._
import io.chekh.keykeeper.domain.key._
import io.chekh.keykeeper.http.responses._
import cats.effect.MonadCancelThrow
import doobie.util.transactor.Transactor
import doobie.implicits._
import doobie.postgres.implicits._
import doobie.util._
import org.typelevel.log4cats.Logger

import java.util.UUID

trait Keys[F[_]] {
  // "algebra"
  def find(id: UUID): F[Option[Key]]

  def find(name: String): F[List[Key]]

  def create(keyInfo: KeyInfo): F[UUID]

  def update(id: UUID, keyInfo: KeyInfo): F[Option[Key]]

  def delete(id: UUID): F[Int]
}

class LiveKeys[F[_] : MonadCancelThrow : Logger] private(xa: Transactor[F]) extends Keys[F] {

  override def find(id: UUID): F[Option[Key]] =
    sql"""
      SELECT
        id,
        name,
        password,
        description,
        created,
        deleted
      FROM keys
      WHERE id = $id
      """
      .query[Key]
      .option
      .transact(xa)

  override def find(name: String): F[List[Key]] =
    sql"""
      SELECT
        id,
        name,
        password,
        description,
        created,
        deleted
      FROM keys
      WHERE name ILIKE ${"%" + name + "%"}
      """
      .query[Key]
      .to[List]
      .transact(xa).logError(e => s"Something wrong: $e")

  override def create(keyInfo: KeyInfo): F[UUID] =
    sql"""
      INSERT INTO keys (
        name,
        password,
        description,
        created,
        deleted
      ) VALUES (
        ${keyInfo.name},
        ${keyInfo.password},
        ${keyInfo.description},
        ${System.currentTimeMillis()},
        NULL
      )
      """
      .update
      .withUniqueGeneratedKeys[UUID]("id")
      .transact(xa)

  override def update(id: UUID, keyInfo: KeyInfo): F[Option[Key]] =
    sql"""
      UPDATE keys
      SET
        name = ${keyInfo.name},
        password = ${keyInfo.password},
        description = ${keyInfo.description}
      WHERE id = ${id}
      """
      .update
      .run
      .transact(xa)
      .flatMap(_ => find(id)) // return the updated key

  override def delete(id: UUID): F[Int] =
    sql"""
      DELETE FROM keys
      WHERE id = ${id}
      """
      .update
      .run
      .transact(xa)
}

object LiveKeys {
  implicit val keyRead: Read[Key] = Read[
    (
      UUID,
        String,
        String,
        String,
        Long,
        Option[Long]
      )
  ].map {
    case (
      id: UUID,
      name: String,
      password: String,
      description: String,
      created: Long,
      deleted: Option[Long]
      ) =>
      Key(
        id = id,
        KeyInfo(
          name = name,
          password = password,
          description = description
        ),
        created = created,
        deleted = deleted
      )
  }

  def apply[F[_]: MonadCancelThrow : Logger](xa: Transactor[F]): F[LiveKeys[F]] = new LiveKeys[F](xa).pure[F]
}

