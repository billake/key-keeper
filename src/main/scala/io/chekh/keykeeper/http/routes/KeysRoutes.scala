package io.chekh.keykeeper.http.routes

import io.chekh.keykeeper.logging.syntax._
import io.chekh.keykeeper.domain.key._
import io.chekh.keykeeper.http.responses._
import io.circe.generic.auto._
import org.http4s.circe.CirceEntityCodec._
import cats.implicits._
import cats.effect._
import org.http4s._
import io.chekh.keykeeper.core._
import org.http4s.dsl._
import org.http4s.server._
import org.http4s.headers._
import org.typelevel.log4cats.Logger
import fs2.Stream
import fs2.data.csv._
import org.typelevel.ci._
import scala.concurrent.duration._


class KeysRoutes[F[_] : Concurrent : Logger : Temporal] private(keys: Keys[F]) extends Http4sDsl[F] {

  // EXPORT: GET /api/keys/export/csv
  private val exportCsvRoute: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / "export" / "csv" =>
      Ok(exportAllToCsv).map(
      _.putHeaders(
      `Content-Type`(MediaType.text.csv),
      `Content-Disposition`("attachment", Map(CIString("filename") -> "keys.csv")))
    )
  }

  private def exportAllToCsv: Stream[F, Byte] = {
    keys.findAll()
      .through(encodeWithoutHeaders[Key].apply[F]())
      .through(fs2.text.utf8.encode)
  }

  // READ: GET /api/keys/uuid
  private val findKeyRoute: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / UUIDVar(id) =>
      keys.find(id).flatMap {
        case Some(key) => Ok(key)
        case None      => NotFound(FailureResponse(s"Record $id not found."))
      }
  }

  // READ: GET /api/keys/lookup
  private val lookupRoute: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / "lookup" / name =>
      keys.find(name).flatMap {
        case Nil  => NotFound(FailureResponse(s"Record with '$name' not found."))
        case list => Ok(list)
      }
  }

  // CREATE: POST /api/keys/create { keyInfo }
  private val createKeyRoute: HttpRoutes[F] = HttpRoutes.of[F] {
    case req @ POST -> Root / "create" =>
      for {
        keyInfo <- req.as[KeyInfo].logError(e => s"Parsing payload failed: $e")
        keyId   <- keys.create(keyInfo)
        resp    <- Created(keyId)
      } yield resp
  }

  // UPDATE: PUT /api/keys/uuid { keyInfo }
  private val updateKeyRoute: HttpRoutes[F] = HttpRoutes.of[F] {
    case req@PUT -> Root / UUIDVar(id) =>
      for {
        keyInfo     <- req.as[KeyInfo]
        maybeNewKey <- keys.update(id, keyInfo)
        resp        <- maybeNewKey match {
          case Some(_) => Ok()
          case None    => NotFound(FailureResponse(s"Cannot update key $id not found."))
        }
      } yield resp
  }

  // DELETE: DELETE /api/keys/uuid
  private val deleteKeyRoute: HttpRoutes[F] = HttpRoutes.of[F] {
    case DELETE -> Root / UUIDVar(id) =>
      keys.delete(id).flatMap {
        case 0 => NotFound(FailureResponse(s"Cannot delete key $id not found."))
        case 1 => Ok()
        case _ => InternalServerError(FailureResponse(s"Cannot delete more than one key."))
      }
  }

  val routes = Router(
    "/keys" -> (findKeyRoute <+> lookupRoute <+> createKeyRoute <+> updateKeyRoute <+> deleteKeyRoute <+> exportCsvRoute)
  )
}

object KeysRoutes {
  def apply[F[_] : Concurrent : Logger : Temporal](keys: Keys[F]) = new KeysRoutes[F](keys)
}
