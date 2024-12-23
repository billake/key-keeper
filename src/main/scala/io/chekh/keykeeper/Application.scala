package io.chekh.keykeeper

import cats.effect._
import io.chekh.keykeeper.config.ConfigModule
import io.chekh.keykeeper.http._
import io.chekh.keykeeper.modules._
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

object Application extends IOApp.Simple {

  implicit val logger: Logger[IO] = Slf4jLogger.getLogger[IO]

  override def run: IO[Unit] = {
    val appResource = for {
      config <- ConfigModule.of[IO]()
      xa     <- Database.makePostgresResource[IO](config.applicationConfig.database)
      core   <- Core[IO](xa)
      _      <- HttpModule.of[IO](config.applicationConfig.http)(core)
    } yield ()

    appResource.use(_ => IO.println("Server ready!") *> IO.never)
  }

}