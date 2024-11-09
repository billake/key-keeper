package io.chekh.keykeeper.fixtures

import io.chekh.keykeeper.domain.key._

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

object KeysFixture {

  val CustomCreatedTime = LocalDateTime.parse("2024-11-09T12:00:00", DateTimeFormatter.ISO_LOCAL_DATE_TIME)

  val SomeKeyUuid = UUID.fromString("a9ec8f5e-2b60-4d54-add7-d7e87a673d7a")

  val SomeKey = Key(
    SomeKeyUuid,
    KeyInfo(
      "some_key_name",
      "some_key_password_123",
      "some_key_description"
    ),
    CustomCreatedTime,
    None
  )

  val NewKeyUuid = UUID.fromString("5e1f30ce-f2ef-4f70-953b-0b58f954c858")

  val UpdatedSomeKey = Key(
    SomeKeyUuid,
    KeyInfo(
      "updated_some_key_name",
      "updated_some_key_password_12356",
      "updated_some_key_description"
    ),
    CustomCreatedTime,
    None
  )

}
