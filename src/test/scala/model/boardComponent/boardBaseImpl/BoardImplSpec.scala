package model.boardComponent.boardBaseImpl

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import model.{Board, Card}

final class BoardImplSpec extends AnyWordSpec with Matchers {

  "BoardImpl" should {

    "delegate choose and update board" in {
      val base = Board(Vector(Card(0,"A"), Card(1,"A")))
      val impl = BoardImpl(base)

      val (b2, res) = impl.choose(0)

      res.shouldBe(None)
      impl.board.cards(0).isFaceUp.shouldBe(true)
    }

    "report allMatched correctly" in {
      val done = Board(Vector(
        Card(0,"A", isFaceUp = true, isMatched = true),
        Card(1,"A", isFaceUp = true, isMatched = true)
      ))
      val impl = BoardImpl(done)

      impl.allMatched.shouldBe(true)
    }

    "allow replacing the board" in {
      val impl = BoardImpl(Board(Vector(Card(0,"A"))))
      val next = Board(Vector(Card(0,"B")))

      impl.board = next
      impl.board.cards.head.symbol.shouldBe("B")
    }
  }
}
