package io.chekh.keykeeper.http.routes

import cats.effect._
import cats.implicits._
import cats.effect.testing.scalatest.AsyncIOSpec
import io.circe.generic.auto._
import io.chekh.keykeeper.core._
import io.chekh.keykeeper.domain.key._
import io.chekh.keykeeper.fixtures.KeysFixture._
import org.http4s.circe.CirceEntityCodec._
import org.http4s._
import org.http4s.dsl._
import org.http4s.implicits._
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.should.Matchers
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

import java.util.UUID

class KeyRoutesSpec extends AsyncFreeSpec with AsyncIOSpec with Matchers with Http4sDsl[IO] {
  val keys: Keys[IO] = new Keys[IO] {
    override def find(id: UUID): IO[Option[Key]] =
      if (id == SomeKeyUuid)
        IO.pure(Some(SomeKey))
      else
        IO.pure(None)

    override def create(keyInfo: KeyInfo): IO[UUID] =
      IO.pure(NewKeyUuid)

    override def update(id: UUID, keyInfo: KeyInfo): IO[Option[Key]] =
      if (id == SomeKeyUuid)
        IO.pure(Some(UpdatedSomeKey))
      else
        IO.pure(None)

    override def delete(id: UUID): IO[Int] =
      if (id == SomeKeyUuid) IO.pure(1)
      else IO.pure(0)

    override def find(name: String): IO[List[Key]] = ???

    override def findAll(): fs2.Stream[IO, Key] = ???

    override def createMany(infos: List[KeyInfo]): fs2.Stream[IO, UUID] = ???
  }

  implicit val logger: Logger[IO] = Slf4jLogger.getLogger[IO]

  val keysRoutes: HttpRoutes[IO] = KeysRoutes[IO](keys).routes

  "KeyRoutes" - {
    "should return a key with a given id" in {
      for {
        response <- keysRoutes.orNotFound.run(
          Request(method = Method.GET, uri = uri"/keys/a9ec8f5e-2b60-4d54-add7-d7e87a673d7a")
        )
        retrieved <- response.as[Key]
      } yield {
        response.status shouldBe Status.Ok
        retrieved shouldBe SomeKey
      }
    }
    "should create a new key" in {
      for {
        response <- keysRoutes.orNotFound.run(
          Request(method = Method.POST, uri = uri"/keys/create")
            .withEntity(SomeKey.keyInfo)
        )
        retrieved <- response.as[UUID]
      } yield {
        response.status shouldBe Status.Created
        retrieved shouldBe NewKeyUuid
      }
    }
    "should update a key that exists" in {
      for {
        responseOk <- keysRoutes.orNotFound.run(
          Request(method = Method.PUT, uri = uri"/keys/a9ec8f5e-2b60-4d54-add7-d7e87a673d7a")
            .withEntity(UpdatedSomeKey.keyInfo)
        )

        responseInvalid <- keysRoutes.orNotFound.run(
          Request(method = Method.PUT, uri = uri"/keys/a9ec8f5e-2b60-4d54-add7-000000000000")
            .withEntity(UpdatedSomeKey.keyInfo)
        )

      } yield {
        responseOk.status shouldBe Status.Ok
        responseInvalid.status shouldBe Status.NotFound
      }
    }
    "should delete a key that exists" in {
      for {
        responseOk <- keysRoutes.orNotFound.run(
          Request(method = Method.DELETE, uri = uri"/keys/a9ec8f5e-2b60-4d54-add7-d7e87a673d7a")
        )

        responseInvalid <- keysRoutes.orNotFound.run(
          Request(method = Method.DELETE, uri = uri"/keys/a9ec8f5e-2b60-4d54-add7-000000000000")
        )

      } yield {
        responseOk.status shouldBe Status.Ok
        responseInvalid.status shouldBe Status.NotFound
      }
    }
  }

}
