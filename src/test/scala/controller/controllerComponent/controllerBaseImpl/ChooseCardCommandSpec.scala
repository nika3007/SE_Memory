package controller.controllerComponent.controllerBaseImpl

import controller.controllerComponent.controllerBaseImpl.{ControllerImpl, ChooseCardCommand}
import model.*
import model.modelComponent.implModel.MemoryGameImpl
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class ChooseCardCommandSpec extends AnyWordSpec with Matchers {

  private def controller(): ControllerImpl =
    val theme = ThemeFactory.getTheme("fruits")
    val ai = NoAI()
    val level = Level(BoardSizes.Small2x2, Difficulties.Easy)
    val game = new MemoryGameImpl(theme, ai, Vector(level))
    game.board = Board(Vector(
      Card(0,"A"), Card(1,"B"),
      Card(2,"A"), Card(3,"C")
    ))
    new ControllerImpl(game)

  "A ChooseCardCommand" should {

    "do, undo and redo a card selection" in {
      val c = controller()
      val cmd = new ChooseCardCommand(c, 0)

      cmd.doStep()
      c.board.cards(0).isFaceUp shouldBe true

      cmd.undoStep()
      c.board.cards(0).isFaceUp shouldBe false

      cmd.redoStep()
      c.board.cards(0).isFaceUp shouldBe true
    }
  }
}
