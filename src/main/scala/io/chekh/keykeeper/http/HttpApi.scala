package io.chekh.keykeeper.http

import cats._
import io.chekh.keykeeper.http.routes._
import cats.implicits._
import org.http4s.server._

class HttpApi[F[_] : Monad] private {
  private val healthRoutes = HealthRoutes[F].routes
  private val keysRoutes = KeysRoutes[F].routes

  val endpoints = Router(
    "/api" -> (healthRoutes <+> keysRoutes)
  )
}

object HttpApi {
  def apply[F[_] : Monad] = new HttpApi[F]
}
