package controller

import model.Board

trait ControllerApi:
  def add(o: Observer): Unit
  def remove(o: Observer): Unit
  
  def board: Board
  def gameStatus: GameStatus
  def currentPlayer: String

  def processInput(input: String): Boolean
  def undo(): Unit
  def redo(): Unit
