package io.chekh.keykeeper.domain

import java.util.UUID

object key {

  final case class Key(
                  id: UUID,
                  keyInfo: KeyInfo,
                  created: Long,
                  deleted: Option[Long])

  final case class KeyInfo(
                      name: String,
                      password: String,
                      description: String)

  object KeyInfo {
    val empty: KeyInfo = KeyInfo("", "", "")
  }
}
