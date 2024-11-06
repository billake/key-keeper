package io.chekh.keykeeper.http.routes

import io.circe.generic.auto._
import org.http4s.circe.CirceEntityCodec._
import cats._
import io.chekh.keykeeper.domain.key._
import org.http4s._
import org.http4s.dsl._
import org.http4s.server._

import java.util.UUID
import scala.collection.mutable

class KeysRoutes[F[_]: Monad] private extends Http4sDsl[F] {
  // "database"
  private val database = mutable.Map[UUID, Key]()

  // READ: GET /api/keys
  private val allKeysRoute: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root => Ok(database.values)
  }

  val routes = Router(
    "/keys" -> allKeysRoute
  )
}

object KeysRoutes {
  def apply[F[_] : Monad] = new KeysRoutes[F]
}
