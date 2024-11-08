package io.chekh.keykeeper.modules

import cats.effect._
import doobie.util.transactor.Transactor
import io.chekh.keykeeper.core._
import org.typelevel.log4cats.Logger

final class Core[F[_]] private(val keys: Keys[F])

object Core {
  def apply[F[_] : Async : Logger](xa: Transactor[F]): Resource[F, Core[F]] =
    Resource.eval(LiveKeys[F](xa))
      .map(keys => new Core(keys))
}
