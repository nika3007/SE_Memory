package controller.controllerComponent.controllerBaseImpl

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

import controller.Controller
import controller.controllerComponent.{ControllerAPI, GameStatus}
import model.*
import model.modelComponent.implModel.MemoryGameImpl

final class ControllerImplSpec extends AnyWordSpec with Matchers {

  private def controller(ai: Boolean = false): ControllerAPI = {
    val theme = ThemeFactory.getTheme("fruits")
    val aiPlayer = if ai then RandomAI() else NoAI()
    val level = Level(BoardSizes.Small2x2, Difficulties.Easy)
    val game = new MemoryGameImpl(theme, aiPlayer, Vector(level))
    Controller(game)
  }

  "A Controller" should {

    "flip a card on valid numeric input" in {
      val c = controller()
      c.processInput("0") shouldBe true
      c.board.cards(0).isFaceUp shouldBe true
    }

    "return false on empty input" in {
      val c = controller()
      c.processInput("") shouldBe false
    }

    "set InvalidSelection on invalid input" in {
      val c = controller()
      c.processInput("xyz")
      c.gameStatus match
        case GameStatus.InvalidSelection(_) => succeed
        case _ => fail("Expected InvalidSelection")
    }

    "undo and redo without crashing" in {
      val c = controller()
      noException shouldBe thrownBy {
       c.undo()
      }

      noException shouldBe thrownBy {
       c.redo()
      }

    }

    "ignore human input when AI is current player" in {
      val c = controller(ai = true)

      c.processInput("0")
      c.processInput("1")
      Thread.sleep(1300) // AI turn

      c.currentPlayer shouldBe "ai"
      val before = c.board.cards.map(_.isFaceUp)
      c.processInput("2")
      c.board.cards.map(_.isFaceUp) shouldBe before
    }
  }
}
