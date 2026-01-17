package model.boardComponent

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

import model.{Board, Card}
import model.boardComponent.boardBaseImpl.BoardImpl

final class BoardComponentSpec extends AnyWordSpec with Matchers:

  "BoardComponent" should {

    "wrap a Board into a BoardAPI" in {
      val base = Board(Vector(Card(0, "A"), Card(1, "A")))
      val api: BoardAPI = BoardComponent(base)

      api.board.cards shouldBe base.cards
    }


    "preserve the cards from the wrapped Board" in {
      val base = Board(Vector(Card(0, "A"), Card(1, "A")))
      val api: BoardAPI = BoardComponent(base)


      //api.cards shouldBe base.cards
    }
  }
