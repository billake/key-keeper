package io.chekh.keykeeper.modules

import cats.effect._
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts

object Database {
  def makePostgresResource[F[_] : Async]: Resource[F, HikariTransactor[F]] = for {
    ec <- ExecutionContexts.fixedThreadPool(32)
    xa <- HikariTransactor.newHikariTransactor[F](
      "org.postgresql.Driver",
      "jdbc:postgresql:board",
      "docker",
      "docker",
      ec
    )
  } yield xa

}
