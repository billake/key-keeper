ThisBuild / version := "0.1.0-SNAPSHOT"
lazy val appName = "key-keeper"

lazy val scala2Version = "2.13.15"
lazy val catsEffectVersion = "3.3.14"
lazy val circeVersion = "0.14.0"
lazy val scalaCsvVersion = "1.3.6"
lazy val http4sVersion = "0.23.15"
lazy val doobieVersion = "1.0.0-RC1"
lazy val tapirVersion = "1.0.0"
lazy val log4catsVersion = "2.4.0"
lazy val logbackVersion = "1.4.0"
lazy val slf4jVersion = "2.0.0"
lazy val scalaTestVersion = "3.2.12"
lazy val scalaTestCatsEffectVersion = "1.4.0"
lazy val pureconfigVersion = "0.17.7"
lazy val fs2Version = "3.2.4"
lazy val fs2DataCsvVersion = "1.11.1"
lazy val testContainerVersion = "1.17.3"

lazy val server = (project in file("."))
  .settings(
    name := appName,
    scalaVersion := scala2Version,
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-effect" % catsEffectVersion,
      "org.http4s" %% "http4s-dsl" % http4sVersion,
      "org.http4s" %% "http4s-ember-server" % http4sVersion,
      "org.http4s" %% "http4s-circe" % http4sVersion,
      "co.fs2" %% "fs2-core" % fs2Version,
      "org.gnieh" %% "fs2-data-csv-generic" % fs2DataCsvVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-core" % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % tapirVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "com.github.tototoshi" %% "scala-csv" % scalaCsvVersion,
      "org.tpolecat" %% "doobie-core" % doobieVersion,
      "org.tpolecat" %% "doobie-hikari" % doobieVersion,
      "org.tpolecat" %% "doobie-postgres" % doobieVersion,
      "com.github.pureconfig" %% "pureconfig" % pureconfigVersion,
      "com.github.pureconfig" %% "pureconfig-magnolia" % pureconfigVersion,
      "org.typelevel" %% "log4cats-slf4j" % log4catsVersion,
      "org.slf4j" % "slf4j-simple" % slf4jVersion,
      "org.typelevel" %% "log4cats-noop" % log4catsVersion % Test,
      "ch.qos.logback" % "logback-classic" % logbackVersion % Test,
      "org.scalatest" %% "scalatest" % scalaTestVersion % Test,
      "org.typelevel" %% "cats-effect-testing-scalatest" % scalaTestCatsEffectVersion % Test,
      "org.testcontainers" % "testcontainers" % testContainerVersion % Test,
      "org.testcontainers" % "postgresql" % testContainerVersion % Test
    )
  )

assembly / test := {}
assembly / assemblyJarName  := s"$appName.jar"
assembly / assemblyMergeStrategy := {
  case PathList("reference.conf") => MergeStrategy.concat
  case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
  case _ => MergeStrategy.first
}