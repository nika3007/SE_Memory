package controller

import util.Observer
import model.Board
import model.MemoryGameAPI


trait ControllerAPI:

  def add(o: Observer): Unit
  def remove(o: Observer): Unit
  def notifyObservers: Unit

  def board: Board
  def processInput(input: String): Boolean
  def undo(): Unit

  def currentPlayer: String
  def gameStatus: GameStatus

  def aiEnabled: Boolean
  def aiTurnFirst(): Unit
  def aiTurnSecond(): Unit
  def game: MemoryGameAPI
