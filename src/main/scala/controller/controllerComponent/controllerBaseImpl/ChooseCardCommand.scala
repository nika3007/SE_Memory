package controller.controllerComponent.controllerBaseImpl

import controller.controllerComponent.{Command, GameStatus}
import model.Board

class ChooseCardCommand(controller: ControllerImpl, index: Int) extends Command:

  private var beforeBoard: Board = null
  private var beforePlayer: String = ""
  private var beforeStatus: GameStatus = GameStatus.Idle

  override def doStep(): Unit =
    // ✅ State sichern, nicht Component
    beforeBoard = controller.board.board
    beforePlayer = controller.currentPlayer
    beforeStatus = controller.gameStatus

    controller.handleCardSelection(index)

  override def undoStep(): Unit =
    controller.cancelThread = true

    // ✅ State kontrolliert über BoardComponent zurücksetzen
    controller.board.board = beforeBoard
    controller._currentPlayer = beforePlayer
    controller._gameStatus = beforeStatus
    controller.notifyObservers

  override def redoStep(): Unit =
    controller.cancelThread = false
    controller.handleCardSelection(index)
