package io.chekh.keykeeper

import cats.effect.{IO, IOApp}
import io.chekh.keykeeper.http.HttpApi
import org.http4s.ember.server.EmberServerBuilder

object Application extends IOApp.Simple {
  override def run: IO[Unit] = EmberServerBuilder
    .default[IO]
    .withHttpApp(HttpApi[IO].endpoints.orNotFound)
    .build
    .use(_ => IO.println("Server ready!") *> IO.never)
}