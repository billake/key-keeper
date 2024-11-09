package io.chekh.keykeeper.domain

import cats.data._
import cats.implicits._
import fs2.data.csv._
import java.util.UUID
import java.time.LocalDateTime

object key {

  final case class Key(
                        id: UUID,
                        keyInfo: KeyInfo,
                        created: LocalDateTime,
                        deleted: Option[LocalDateTime])


  object Key {
    implicit val keyRowEncoder: RowEncoder[Key] = RowEncoder.instance { key =>
      NonEmptyList.of(
        key.id.toString,
        key.keyInfo.name,
        key.keyInfo.password,
        key.keyInfo.description,
        key.created.toString,
        key.deleted.map(_.toString).getOrElse("")
      )
    }
  }

  final case class KeyInfo(
                            name: String,
                            password: String,
                            description: String)

  object KeyInfo {
    val empty: KeyInfo = KeyInfo("", "", "")

    implicit val keyRowDecoder: RowDecoder[KeyInfo] = RowDecoder.instance[KeyInfo] { row =>
      row.values.toList match {
        case List(name, password, description) => KeyInfo(name, password, description).asRight
        case l => new DecoderError(s"Illegal number of elements $l").asLeft[KeyInfo]
      }
    }
  }

}
