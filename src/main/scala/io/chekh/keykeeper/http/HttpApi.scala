package io.chekh.keykeeper.http

import cats._
import cats.effect._
import io.chekh.keykeeper.http.routes._
import cats.implicits._
import org.http4s.server._
import org.typelevel.log4cats.Logger

class HttpApi[F[_] : Concurrent : Logger] private {
  private val healthRoutes = HealthRoutes[F].routes
  private val keysRoutes = KeysRoutes[F].routes

  val endpoints = Router(
    "/api" -> (healthRoutes <+> keysRoutes)
  )
}

object HttpApi {
  def apply[F[_] : Concurrent : Logger] = new HttpApi[F]
}
