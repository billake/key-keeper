package io.chekh.keykeeper.domain

import java.util.UUID

object key {

  case class Key(
                id: UUID,
                name: String,
                password: String,
                description: String,
                created: Long,
                deleted: Option[Long]
                )

}
