package io.chekh.keykeeper.http.routes

import io.chekh.keykeeper.logging.syntax._
import io.circe.generic.auto._
import org.http4s.circe.CirceEntityCodec._
import io.chekh.keykeeper.domain.key._
import cats._
import cats.implicits._
import cats.effect._
import org.http4s._
import io.chekh.keykeeper.core._
import org.http4s.dsl._
import org.http4s.dsl.impl._
import org.http4s.server._
import org.typelevel.log4cats.Logger

class KeysRoutes[F[_] : Concurrent : Logger] private (keys: Keys[F]) extends Http4sDsl[F] {

  // READ: GET /api/keys/uuid
  private val findKeyRoute: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / UUIDVar(id) =>
      keys.find(id).flatMap {
        case Some(key) => Ok(key)
        case None      => NotFound(s"Record $id not found.")
      }
  }

  // CREATE: POST /api/keys/create { keyInfo }
  private val createKeyRoute: HttpRoutes[F] = HttpRoutes.of[F] {
    case req @ POST -> Root / "create" =>
      for {
        keyInfo <- req.as[KeyInfo].logError(e => s"Parsing payload failed: $e")
        keyId <- keys.create(keyInfo)
        resp <- Created(keyId)
      } yield resp
  }

  // UPDATE: PUT /api/keys/uuid { keyInfo }
  private val updateKeyRoute: HttpRoutes[F] = HttpRoutes.of[F] {
    case req @ PUT -> Root / UUIDVar(id) =>
          for {
            keyInfo <- req.as[KeyInfo]
            maybeNewKey <- keys.update(id, keyInfo)
            resp <- maybeNewKey match {
              case Some(_) => Ok()
              case None => NotFound(s"Cannot update key $id not found.")
          }
      } yield resp
  }

  // DELETE: DELETE /api/keys/uuid
  private val deleteKeyRoute: HttpRoutes[F] = HttpRoutes.of[F] {
    case req @ DELETE -> Root / UUIDVar(id) =>
      keys.find(id).flatMap {
        case Some(key) =>
          for {
            _ <- keys.delete(id)
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
  def apply[F[_] : Concurrent : Logger](keys: Keys[F]) = new KeysRoutes[F](keys)
}
