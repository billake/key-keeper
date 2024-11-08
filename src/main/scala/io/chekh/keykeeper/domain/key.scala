package io.chekh.keykeeper.domain

import cats.data._
import java.util.UUID
import cats.implicits._
import fs2.data.csv._

object key {

  final case class Key(
                        id: UUID,
                        keyInfo: KeyInfo,
                        created: Long,
                        deleted: Option[Long])


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
        case _ => new DecoderError("Illegal number of elements").asLeft[KeyInfo]
      }
    }
  }

}
