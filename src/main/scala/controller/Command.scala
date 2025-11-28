package controller

import model.{GameMemento, MemoryGame}

// command eigenschaften
trait Command:
  def doStep(): Unit
  def undoStep(): Unit

// wähle karte --> veränderung "Basis" der do bzw undo
final class ChooseCardCommand(controller: Controller, index: Int) extends Command:

  // merke das board bevor irgendwas gemacht wird
  private val before: GameMemento = controller.game.save()

  override def doStep(): Unit =
    // cardHandler wird benutzt damit ein "do" passieren kann
    controller.handleCardSelection(index)

  override def undoStep(): Unit =
    // weiederherstellung vom vorherigen zustand des boards
    controller.game.restore(before)
