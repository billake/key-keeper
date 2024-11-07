package io.chekh.keykeeper.config

import cats.implicits._
import cats.effect.Resource
import cats.effect.Sync
import pureconfig.ConfigSource

trait ConfigModule {
  def applicationConfig: ApplicationConfig
}

object ConfigModule {

  def of[F[_] : Sync](): Resource[F, ConfigModule] =
    Resource.eval(
      Sync[F].delay(
        ConfigSource.default.loadOrThrow[ApplicationConfig]
      ).map { config =>
        new ConfigModule {
          override def applicationConfig: ApplicationConfig = config
        }
      }
    )

}
