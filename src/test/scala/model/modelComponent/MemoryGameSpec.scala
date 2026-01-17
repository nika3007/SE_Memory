package model.modelComponent

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

import model.*
import model.modelComponent.implModel.MemoryGameImpl

class MemoryGameSpec extends AnyWordSpec with Matchers {

  private def game(levels: Vector[Level]) =
    new MemoryGameImpl(
      ThemeFactory.getTheme("fruits"),
      NoAI(),
      levels
    )

  "A MemoryGame" should {

    "setTheme should rebuild the board" in {
      val g = game(Vector(Level(BoardSizes.Small2x2, Difficulties.Easy)))
      val before = g.board.cards.map(_.symbol)

      g.setTheme("animals")
      val after = g.board.cards.map(_.symbol)

      before should not equal after
    }

    "setAI should switch AI implementations and fallback" in {
      val g = game(Vector(Level(BoardSizes.Small2x2, Difficulties.Easy)))

      g.setAI("easy")
      g.ai shouldBe a [RandomAI]

      g.setAI("invalid")
      g.ai shouldBe a [RandomAI]
    }

    "nextLevel should advance if possible" in {
      val g = game(Vector(
        Level(BoardSizes.Small2x2, Difficulties.Easy),
        Level(BoardSizes.Small2x2, Difficulties.Easy)
      ))

      g.nextLevel() shouldBe true
      g.currentLevelIndex shouldBe 1
    }

    "nextLevel should return false if no next level exists" in {
      val g = game(Vector(Level(BoardSizes.Small2x2, Difficulties.Easy)))

      g.nextLevel() shouldBe false
    }

    "save and restore should restore board state" in {
      val g = game(Vector(Level(BoardSizes.Small2x2, Difficulties.Easy)))
      val m = g.save()

      g.board = g.board.flipAt(0)
      g.restore(m)

      g.board.cards shouldBe m.board.cards
    }
  }
}
