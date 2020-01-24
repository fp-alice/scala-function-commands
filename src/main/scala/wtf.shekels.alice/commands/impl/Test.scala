package wtf.shekels.alice.commands.impl

import cats.effect.IO
import wtf.shekels.alice.annotations.CommandHandler
import wtf.shekels.alice.commands.Command
import wtf.shekels.alice.commands.CommandParser._
import shapeless._

@CommandHandler
class Test {
  def add: Command[(Int, Int)] = Command {
    case (x: Int, y: Int) => IO(println(x + y))
  }

  def sub: Command[(Int, Int)] = Command {
    case (x: Int, y: Int) => IO(println(x - y))
  }

  def mul: Command[Int :: Int :: HNil] = Command {
    case (x :: y :: HNil) => IO(println(x * y))
  }
}
