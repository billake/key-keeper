package io.chekh.keykeeper.core

import cats.effect._
import cats.effect.testing.scalatest.AsyncIOSpec
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.should.Matchers
import io.chekh.keykeeper.fixtures.KeysFixture._
import doobie.postgres.implicits._
import doobie.util._
import doobie.implicits._
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

class KeysSpec extends AsyncFreeSpec with AsyncIOSpec with Matchers with DoobieSpec {

  override val initScript: String = "sql/keys.sql"
  implicit val logger: Logger[IO] = Slf4jLogger.getLogger[IO]

  "Keys 'algebra'" - {
    "should return no key if given UUID does not exist" in {
      transactor.use { xa =>
        val program = for {
          keys      <- LiveKeys[IO](xa)
          retrieved <- keys.find(NotFoundKeyUuid)
        } yield retrieved

        program.asserting(_ shouldBe None)
      }
    }
    "should retrieve a key by id" in {
      transactor.use { xa =>
        val program = for {
          keys      <- LiveKeys[IO](xa)
          retrieved <- keys.find(SomeKeyUuid)
        } yield retrieved

        program.asserting(_ shouldBe Some(SomeKey))
      }
    }
    "should create a key" in {
      transactor.use { xa =>
        val program = for {
          keys     <- LiveKeys[IO](xa)
          keyId    <- keys.create(NewSomeKey)
          maybeKey <- keys.find(keyId)
        } yield maybeKey

        program.asserting(_.map(_.keyInfo) shouldBe Some(NewSomeKey))
      }
    }
    "should return an updated key if it exists" in {
      transactor.use { xa =>
        val program = for {
          keys            <- LiveKeys[IO](xa)
          maybeUpdatedKey <- keys.update(SomeKeyUuid, UpdatedSomeKey.keyInfo)
        } yield maybeUpdatedKey

        program.asserting(_ shouldBe Some(UpdatedSomeKey))
      }
    }
    "should return None trying to update a key that does not exist" in {
      transactor.use { xa =>
        val program = for {
          keys            <- LiveKeys[IO](xa)
          maybeUpdatedKey <- keys.update(NotFoundKeyUuid, UpdatedSomeKey.keyInfo)
        } yield maybeUpdatedKey

        program.asserting(_ shouldBe None)
      }
    }
    "should delete an existing key" in {
      transactor.use { xa =>
        val program = for {
          keys                <- LiveKeys[IO](xa)
          numberOfDeletedKeys <- keys.delete(SomeKeyUuid)
          countOfLKeys        <- sql"SELECT COUNT(*) FROM keys WHERE id = $SomeKeyUuid"
            .query[Int]
            .unique
            .transact(xa)
        } yield (numberOfDeletedKeys, countOfLKeys)

        program.asserting { case (numberOfDeletedKeys, countOfLKeys) =>
          numberOfDeletedKeys shouldBe 1
          countOfLKeys shouldBe 0
        }
      }
    }
    "should return zero updated rows if the key ID to delete is not found" in {
      transactor.use { xa =>
        val program = for {
          keys                <- LiveKeys[IO](xa)
          numberOfDeletedKeys <- keys.delete(NotFoundKeyUuid)
        } yield numberOfDeletedKeys

        program.asserting(_ shouldBe 0)
      }
    }
  }

}
