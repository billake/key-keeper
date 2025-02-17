package io.chekh.keykeeper.http.routes

import org.http4s.circe.CirceEntityCodec._
import cats._
import org.http4s._
import org.http4s.dsl._
import org.http4s.server._

class HealthRoutes[F[_] : Monad] private extends Http4sDsl[F] {
  private val healthRoute: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root =>
      Ok("All going great!")
  }

  val routes = Router(
    "/health" -> healthRoute
  )
}

object HealthRoutes {
  def apply[F[_] : Monad] = new HealthRoutes[F]
}
