package controller.controllerComponent.controllerBaseImpl

import controller.Command
import model.GameMemento
import scala.util.Try

final class ChooseCardCommand(
  controller: ControllerImpl,
  index: Int
) extends Command:

  private val before: GameMemento = controller.game.save()

  override def doStep(): Try[Unit] =
    Try(controller.handleCardSelection(index))

  override def undoStep(): Try[Unit] =
    Try(controller.game.restore(before))