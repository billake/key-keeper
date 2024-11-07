package io.chekh.keykeeper.config

import pureconfig.ConfigReader
import pureconfig.generic.auto.exportReader
import io.chekh.keykeeper.config.HttpConfig._

final case class ApplicationConfig(http: HttpConfig, database: DatabaseConfig)

object ApplicationConfig {

  implicit val configReader: ConfigReader[ApplicationConfig] = exportReader[ApplicationConfig].instance

}
