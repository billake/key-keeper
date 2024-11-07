package io.chekh.keykeeper.config

import com.comcast.ip4s.{Host, Port}
import pureconfig.ConfigReader
import pureconfig.error.CannotConvert

final case class HttpConfig(host: Host, port: Port)

object HttpConfig {

  implicit val hostConfigReader: ConfigReader[Host] = ConfigReader[String].emap { hostValue =>
    Host.fromString(hostValue).toRight(CannotConvert(hostValue, "com.comcast.ip4s.Host", "Can not parse host"))
  }

  implicit val portConfigReader: ConfigReader[Port] =
    ConfigReader[Int].emap { portValue =>
      Port.fromInt(portValue).toRight(CannotConvert(portValue.toString, "com.comcast.ip4s.Port", "Can not parse port"))
    }

}