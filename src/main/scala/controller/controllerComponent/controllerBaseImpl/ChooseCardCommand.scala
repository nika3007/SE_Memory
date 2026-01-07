package controller.controllerComponent.controllerBaseImpl

import controller.controllerComponent.Command
import controller.controllerComponent.GameStatus
import model.Board

class ChooseCardCommand(controller: ControllerImpl, index: Int) extends Command:

  private var beforeBoard: Board = null
  private var beforePlayer: String = ""
  private var beforeStatus: GameStatus = GameStatus.Idle

  override def doStep(): Unit =
    beforeBoard = controller.board.copy()
    beforePlayer = controller.currentPlayer
    beforeStatus = controller.gameStatus

    controller.handleCardSelection(index)

  override def undoStep(): Unit =
    controller.cancelThread = true
    controller.game.board = beforeBoard
    controller._currentPlayer = beforePlayer
    controller._gameStatus = beforeStatus
    controller.notifyObservers

  override def redoStep(): Unit =
    controller.cancelThread = false
    controller.handleCardSelection(index)
