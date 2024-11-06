package io.chekh.keykeeper

import cats.effect.{IO, IOApp}
import io.chekh.keykeeper.http.HttpApi
import org.http4s.ember.server.EmberServerBuilder
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

object Application extends IOApp.Simple {

  implicit val logger: Logger[IO] = Slf4jLogger.getLogger[IO]

  override def run: IO[Unit] = EmberServerBuilder
    .default[IO]
    .withHttpApp(HttpApi[IO].endpoints.orNotFound)
    .build
    .use(_ => IO.println("Server ready!") *> IO.never)
}