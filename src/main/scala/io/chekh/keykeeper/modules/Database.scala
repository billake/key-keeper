package io.chekh.keykeeper.modules

import cats.effect._
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import io.chekh.keykeeper.config.DatabaseConfig

object Database {
  def makePostgresResource[F[_] : Async](config: DatabaseConfig): Resource[F, HikariTransactor[F]] = for {
    ec <- ExecutionContexts.fixedThreadPool(config.nThreads)
    xa <- HikariTransactor.newHikariTransactor[F](
      config.driver,
      config.url,
      config.user,
      config.pass,
      ec)
  } yield xa
}
