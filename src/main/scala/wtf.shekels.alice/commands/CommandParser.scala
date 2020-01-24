package wtf.shekels.alice.commands

import atto.Atto._
import atto.{Parser, _}
import cats.implicits._
import shapeless.{::, HList, HNil}

object CommandParser {
  implicit val wordParser: Parser[String] = manyUntil(letterOrDigit, whitespace).map(_.mkString)
  implicit val intParser: Parser[Int] = int
  implicit def tupleParser[A, B](implicit pa: Parser[A], pb: Parser[B]): Parser[(A, B)] = (pa <* skipWhitespace) ~ pb
  implicit val parseHNil: Parser[HNil] = HNil.pure[Parser]
  implicit def parseHList[H, T <: HList](implicit ph: Parser[H], pt: Parser[T]): Parser[H :: T] = {
    ph.flatMap(h => skipWhitespace *> pt.map(t => h :: t))
  }
  implicit def parseList[A](implicit p: Parser[A]): Parser[List[A]] = sepBy1(p, whitespace).map(_.toList)

}
