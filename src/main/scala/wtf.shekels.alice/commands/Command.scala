package wtf.shekels.alice.commands

import atto.Parser
import cats.effect.IO

case class Command[I](run: I => IO[Unit])(implicit val parser: Parser[I]) {}
