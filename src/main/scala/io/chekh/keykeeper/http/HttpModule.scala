package io.chekh.keykeeper.http

import cats.effect._
import io.chekh.keykeeper.config.HttpConfig
import io.chekh.keykeeper.modules.Core
import org.http4s.ember.server.EmberServerBuilder
import org.typelevel.log4cats.Logger

object HttpModule {
  def of[F[_] : Async : Logger](config: HttpConfig)(core: Core[F]): Resource[F, Unit] = for {
    httpApi <- HttpApi[F](core)
    _ <- EmberServerBuilder
      .default[F]
      .withPort(config.port)
      .withHost(config.host)
      .withHttpApp(httpApi.endpoints.orNotFound)
      .build
  } yield ()
}
