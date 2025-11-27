package controller

import model.{GameMemento, MemoryGame}

/** Base command trait */
trait Command:
  def doStep(): Unit
  def undoStep(): Unit

/** One concrete command: choosing a card */
final class ChooseCardCommand(controller: Controller, index: Int) extends Command:

  // snapshot of game *before* the move
  private val before: GameMemento = controller.game.save()

  override def doStep(): Unit =
    // use the controller's card handling (must be visible: private[controller])
    controller.handleCardSelection(index)

  override def undoStep(): Unit =
    // restore previous board state
    controller.game.restore(before)
