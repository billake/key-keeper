package io.chekh.keykeeper.http.routes

import io.chekh.keykeeper.logging.syntax._
import io.circe.generic.auto._
import org.http4s.circe.CirceEntityCodec._
import io.chekh.keykeeper.domain.key._
import cats._
import cats.implicits._
import cats.effect._
import org.http4s._
import org.http4s.dsl._
import org.http4s.dsl.impl._
import org.http4s.server._
import org.typelevel.log4cats.Logger


import java.util.UUID
import scala.collection.mutable

class KeysRoutes[F[_] : Concurrent : Logger] private extends Http4sDsl[F] {
  // "database"
  private val database = mutable.Map[UUID, Key]()

  // READ: GET /api/keys/uuid
  private val findKeyRoute: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / UUIDVar(id) =>
      database.get(id) match {
        case Some(key) => Ok(key)
        case None => NotFound(s"Record $id not found.")
      }
  }

  // CREATE: POST /api/keys/create { keyInfo }
  private def createKey(keyInfo: Key): F[Key] = Key(
    id = UUID.randomUUID(),
    name = "testName",
    password = "testPassword",
    description = "testDescription",
    created = System.currentTimeMillis(),
    deleted = None
  ).pure[F]

  private val createKeyRoute: HttpRoutes[F] = HttpRoutes.of[F] {
    case req@POST -> Root / "create" =>
      for {
        keyInfo <- req.as[Key].logError(e => s"Parsing payload failed: $e")
        //      key     <- createKey(keyInfo)
        _ <- Logger[F].info(s"Create key request received")
        _ <- database.put(keyInfo.id, keyInfo).pure[F]
        resp <- Created(keyInfo.id)
      } yield resp
  }

  // UPDATE: PUT /api/keys/uuid { keyInfo }
  private val updateKeyRoute: HttpRoutes[F] = HttpRoutes.of[F] {
    case req@PUT -> Root / UUIDVar(id) =>
      database.get(id) match {
        case Some(_) =>
          for {
            updatedKey <- req.as[Key]
            _ <- database.put(id, updatedKey).pure[F]
            resp <- Ok()
          } yield resp
        case None => NotFound(s"Cannot update key $id not found.")
      }
  }

  // DELETE: DELETE /api/keys/uuid
  private val deleteKeyRoute: HttpRoutes[F] = HttpRoutes.of[F] {
    case req@DELETE -> Root / UUIDVar(id) =>
      database.get(id) match {
        case Some(_) =>
          for {
            _ <- database.remove(id).pure[F]
            resp <- Ok()
          } yield resp
        case None => NotFound(s"Cannot delete key $id not found.")
      }
  }

  val routes = Router(
    "/keys" -> (findKeyRoute <+> createKeyRoute <+> updateKeyRoute <+> deleteKeyRoute)
  )
}

object KeysRoutes {
  def apply[F[_] : Concurrent : Logger] = new KeysRoutes[F]
}
