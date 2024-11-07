package io.chekh.keykeeper

import cats.effect._
import cats.implicits._
import io.chekh.keykeeper.modules._
import org.http4s.ember.server.EmberServerBuilder
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

object Application extends IOApp.Simple {

  implicit val logger: Logger[IO] = Slf4jLogger.getLogger[IO]

  override def run: IO[Unit] = {
    val appResource = for {
      xa <- Database.makePostgresResource[IO]
      core <- Core[IO](xa)
      httpAri <- HttpApi[IO](core)
      server <- EmberServerBuilder
        .default[IO]
        .withHttpApp(httpAri.endpoints.orNotFound)
        .build
    } yield server

    appResource.use(_ => IO.println("Server ready!") *> IO.never)
  }
}