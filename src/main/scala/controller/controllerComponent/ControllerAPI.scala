package controller.controllerComponent

import util.Observer
import model.modelComponent.MemoryGameAPI
import model.boardComponent.BoardAPI
import controller.controllerComponent.GameStatus

trait ControllerAPI:
  def add(o: Observer): Unit
  def remove(o: Observer): Unit
  def notifyObservers: Unit

  def board: BoardAPI
  def processInput(input: String): Boolean
  def undo(): Unit
  def redo(): Unit


  def currentPlayer: String
  def gameStatus: GameStatus

  def aiEnabled: Boolean
  def aiTurnFirst(): Unit
  def aiTurnSecond(): Unit

  def game: MemoryGameAPI
