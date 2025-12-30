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
  }
}
