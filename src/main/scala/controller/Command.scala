package controller

import model.{GameMemento, MemoryGame}
import scala.util.{Try, Success, Failure}

// command eigenschaften
trait Command:
  def doStep(): Try[Unit]
  def undoStep(): Try[Unit]

// wähle karte --> veränderung base von do bzw undo
final class ChooseCardCommand(controller: Controller, index: Int) extends Command:

  // merke das board bevor irgendwas gemacht wird
  private val beforeTry: Try[GameMemento] = Try(controller.game.save())

  override def doStep(): Try[Unit] =
    // cardHandler wird benutzt damit ein "do" passieren kann
    for
      _ <- beforeTry                               // speicherzustand erfolgreich vorhanden?
      _ <- Try(controller.handleCardSelection(index))
    yield ()

  override def undoStep(): Try[Unit] =
    // weiederherstellung vom vorherigen zustand des boards
    for
      before <- beforeTry
      _ <- Try(controller.game.restore(before))
    yield ()
