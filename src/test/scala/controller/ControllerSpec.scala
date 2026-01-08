package controller.controllerComponent

import controller.controllerComponent.controllerBaseImpl.ControllerImpl
import model.*
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class ControllerSpec extends AnyWordSpec with Matchers {

  private def controller(ai: Boolean = true): ControllerImpl = {
    val theme = ThemeFactory.getTheme("fruits")
    val aiPlayer = if ai then RandomAI() else NoAI()
    val level = Level(BoardSizes.Small2x2, Difficulties.Easy)
    val game = MemoryGame(theme, aiPlayer, Vector(level))
    val c = ControllerImpl(game)
    c.game.board = Board(Vector(
      Card(0,"A"), Card(1,"B"),
      Card(2,"A"), Card(3,"C")
    ))
    c
  }

  "A Controller" should {

    "flip first card" in {
      val c = controller()
      c.processInput("0")
      c.board.cards(0).isFaceUp shouldBe true
    }

    "keep mismatched cards face up (flip-back handled elsewhere)" in {
      val c = controller()
      c.processInput("0")
      c.processInput("1")

      c.board.cards(0).isFaceUp shouldBe true
      c.board.cards(1).isFaceUp shouldBe true
    }

    "stay in NoMatch state after mismatch" in {
      val c = controller()
      c.processInput("0")
      c.processInput("1")

      c.gameStatus shouldBe GameStatus.NoMatch
    }

    "not switch player automatically after NoMatch" in {
      val c = controller(ai = true)
      c.processInput("0")
      c.processInput("1")

      c.currentPlayer shouldBe "human"
    }

    "not crash on undo with empty stack" in {
      val c = controller()
      noException shouldBe thrownBy {
        c.undo()
      }
    }

    "not crash on redo with empty stack" in {
      val c = controller()
      noException shouldBe thrownBy {
        c.redo()
      }
    }

    "handle empty input" in {
      val c = controller()
      c.processInput("") shouldBe false
    }

    "set InvalidSelection on out-of-range index" in {
      val c = controller()
      c.processInput("99")
      c.gameStatus shouldBe GameStatus.InvalidSelection(-1)
    }

    "allow AI to take turns" in {
      val c = controller(ai = true)

      // Mensch spielt falsch → NoMatch → AI dran
      c.processInput("0")
      c.processInput("1")

      Thread.sleep(1300)

      c.currentPlayer shouldBe "ai"

      noException shouldBe thrownBy {
        c.aiTurnFirst()
      }
      noException shouldBe thrownBy {
        c.aiTurnSecond()
      }
    }

    "advance level when all cards are matched" in {
      val c = controller(ai = false)

      c.processInput("0")
      c.processInput("2") // Match A-A

      c.processInput("1")
      c.processInput("3") // Match B-C? (wenn passend anpassen)

      c.gameStatus should (be (GameStatus.Match) or be (GameStatus.LevelComplete))
    }

    "return false on empty input" in {
      val c = controller()
      c.processInput("") shouldBe false
    }

    "return false on null input" in {
      val c = controller()
      c.processInput(null) shouldBe false
    }

    "ignore input when current player is AI" in {
      val c = controller(ai = true)

      // erzwinge AI-Zug
      c.processInput("0")
      c.processInput("1")
      Thread.sleep(1300)

      c.currentPlayer shouldBe "ai"

      val before = c.board.cards.map(_.isFaceUp)
      c.processInput("2")
      c.board.cards.map(_.isFaceUp) shouldBe before
    }

    "not execute aiTurnFirst when player is human" in {
      val c = controller(ai = true)
      noException shouldBe thrownBy {
        c.aiTurnFirst()
      }
    }

    "not execute aiTurnSecond when player is human" in {
      val c = controller(ai = true)
      noException shouldBe thrownBy {
        c.aiTurnSecond()
      }
    }

    "cancel flip-back thread on undo" in {
      val c = controller(ai = false)

      c.processInput("0")
      c.processInput("1") // NoMatch

      c.undo() // setzt cancelThread = true

      Thread.sleep(1300)

      c.gameStatus should not be GameStatus.NextRound
    }
  }
}
