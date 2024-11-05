ThisBuild / version := "0.1.0-SNAPSHOT"

lazy val scala2Version = "2.13.8"
lazy val catsEffectVersion = "3.3.14"
lazy val circeVersion = "0.14.0"
lazy val scalaCsvVersion  = "1.3.6"
lazy val http4sVersion = "0.23.15"
lazy val doobieVersion = "1.0.0-RC1"
lazy val log4catsVersion = "2.4.0"
lazy val logbackVersion = "1.4.0"
lazy val scalaTestVersion = "3.2.12"

lazy val server = (project in file("."))
  .settings(
    name := "key-keeper",
    scalaVersion := scala2Version,
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-effect" % catsEffectVersion,
      "org.http4s" %% "http4s-dsl" % http4sVersion,
      "org.http4s" %% "http4s-ember-server" % http4sVersion,
      "org.http4s" %% "http4s-circe" % http4sVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "com.github.tototoshi" %% "scala-csv" % scalaCsvVersion,
      "org.tpolecat" %% "doobie-core" % doobieVersion,
      "org.tpolecat" %% "doobie-postgres" % doobieVersion,
      "org.typelevel" %% "log4cats-noop" % log4catsVersion % Test,
      "org.scalatest" %% "scalatest" % scalaTestVersion % Test
    )
  )