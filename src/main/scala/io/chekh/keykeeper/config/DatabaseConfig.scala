package io.chekh.keykeeper.config

final case class DatabaseConfig(
   driver: String,
   url: String,
   user: String,
   pass: String)