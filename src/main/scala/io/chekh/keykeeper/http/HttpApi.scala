package io.chekh.keykeeper.http

import cats.effect._
import cats.implicits._
import io.chekh.keykeeper.http.routes._
import io.chekh.keykeeper.modules.Core
import org.http4s.server._
import org.typelevel.log4cats.Logger

class HttpApi[F[_] : Concurrent : Logger] private(core: Core[F]) {

  private val healthRoutes = HealthRoutes[F].routes
  private val keysRoutes = KeysRoutes[F](core.keys).routes

  val endpoints = Router(
    "/api" -> (healthRoutes <+> keysRoutes)
  )
}

object HttpApi {
  def apply[F[_] : Concurrent : Logger](core: Core[F]): Resource[F, HttpApi[F]] =
    Resource.pure(new HttpApi[F](core))
}
