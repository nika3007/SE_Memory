package controller

import model.{GameMemento, MemoryGame}
import scala.util.{Try, Success, Failure}

// command eigenschaften
trait Command:
  def doStep(): Try[Option[Unit]]
  def undoStep(): Try[Option[Unit]]

// wähle karte --> veränderung base von do bzw undo
final class ChooseCardCommand(controller: Controller, index: Int) extends Command:

  // merke das board bevor irgendwas gemacht wird
  private val beforeTry: Try[GameMemento] = Try(controller.game.save())

  override def doStep(): Try[Option[Unit]] =
    // cardHandler wird benutzt damit ein "do" passieren kann
    for
      before <- beforeTry
      result <- Try {
        val oldBoard = controller.board
        controller.handleCardSelection(index)
        val changed = controller.board != oldBoard
        if changed then Some(()) else None
      }
    yield result

  override def undoStep(): Try[Option[Unit]] =
    // weiederherstellung vom vorherigen zustand des boards
    for
      before <- beforeTry
      _ <- Try(controller.game.restore(before))
    yield Some (())
