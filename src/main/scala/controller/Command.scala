package controller

import controller.controllerComponent.ControllerAPI
import model.GameMemento
import scala.util.Try

trait Command:
  def doStep(): Try[Unit]
  def undoStep(): Try[Unit]

final class ChooseCardCommand(controller: ControllerAPI, index: Int) extends Command:
  private val beforeTry: Try[GameMemento] = Try(controller.game.save())

  override def doStep(): Try[Unit] =
    // IMPORTANT: use processInput only (keeps internals hidden)
    Try(controller.processInput(index.toString)).map(_ => ())

  override def undoStep(): Try[Unit] =
    for
      m <- beforeTry
      _ <- Try(controller.game.restore(m))
    yield ()
