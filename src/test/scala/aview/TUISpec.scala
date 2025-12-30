package aview

import controller.Controller
import controller.controllerComponent.ControllerAPI
import model.*
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class MemoryTuiSpec extends AnyWordSpec with Matchers {

  private def controllerWithBoard(board: Board): ControllerAPI = {
    val theme = ThemeFactory.getTheme("fruits")
    val ai = NoAI()
    val level = Level(BoardSizes.Small2x2, Difficulties.Easy)
    val game = MemoryGame(theme, ai, Vector(level))
    val c = Controller(game)
    c.game.board = board
    c
  }

  "A Memory Tui" should {

    "flip a card on numeric input" in {
      val c = controllerWithBoard(
        Board(Vector(
          Card(0,"A"), Card(1,"B"),
          Card(2,"A"), Card(3,"C")
        ))
      )
      val tui = new MemoryTui(c)

      tui.processInputLine("0")

      c.board.cards(0).isFaceUp shouldBe true
    }

    "keep mismatched cards face up after NoMatch (controller does not flip back)" in {
      val c = controllerWithBoard(
        Board(Vector(
          Card(0,"A"), Card(1,"B"),
          Card(2,"A"), Card(3,"C")
        ))
      )
      val tui = new MemoryTui(c)

      tui.processInputLine("0")
      tui.processInputLine("1") // NoMatch

      c.board.cards(0).isFaceUp shouldBe true
      c.board.cards(1).isFaceUp shouldBe true
    }
  }
}
