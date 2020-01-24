package wtf.shekels.alice

import atto.Atto._
import atto.ParseResult.{Done, Fail}
import atto._
import cats.effect.IO
import cats.implicits._
import wtf.shekels.alice.commands.LabelledCommand
import wtf.shekels.alice.reflection.CommandHandlerLoader

object Main {


  def commandParser[I](cmd: LabelledCommand[I]): Parser[IO[Unit]] = {
    val p = string(cmd.className.toLowerCase) *> skipWhitespace *> string(cmd.commandName) *> skipWhitespace *> cmd.command.parser
    p.map(cmd.command.run)
  }

  def runCommand(s: String, p: Parser[IO[Unit]]): IO[Unit] = {
    p.parseOnly(s) match {
      case Done(_, r) => r
      case f@Fail(_, _, _) => IO(println(f))
    }
  }

  def repl(p: Parser[IO[Unit]]): IO[Unit] = for {
    _ <- IO(print("$ "))
    in <- IO(scala.io.StdIn.readLine())
    _ <- IO(print("> "))
    _ <- runCommand(in, p)
    _ <- repl(p)
  } yield ()

  def main(args: Array[String]): Unit = {

    CommandHandlerLoader
      .load
      .map(commands => choice(commands.map(c => commandParser(c))))
      .flatMap(parser => repl(parser))
      .unsafeRunSync()

  }
}
