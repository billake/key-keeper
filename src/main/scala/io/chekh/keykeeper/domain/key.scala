package io.chekh.keykeeper.domain

import java.util.UUID

object key {

  case class Key(
                  id: UUID,
                  keyInfo: KeyInfo,
                  created: Long,
                  deleted: Option[Long])

  case class KeyInfo(
                      name: String,
                      password: String,
                      description: String)

  object KeyInfo {
    val empty: KeyInfo = KeyInfo("", "", "")
  }
}
