package controller

import model.Card
import model.Board
import model.MemoryGame

import util.Observable

class Controller(rows: Int, cols: Int) extends Observable:

  private var game = MemoryGame(rows, cols)
  private var lastResult: Option[Boolean] = None

  def chooseCard(index: Int): Unit =
    val (newBoard, result) = game.board.choose(index)
    game.board = newBoard
    lastResult = result
    notifyObservers()

    // Wenn kein Match, Karten zurückdrehen nach kurzer Zeit
    result match
      case Some(false) =>
        Thread.sleep(1500)
        val flippedBack = game.board.cards.map {
          case c if c.isFaceUp && !c.isMatched => c.flip
          case c => c
        }
        game.board = Board(flippedBack)
        notifyObservers()
      case _ => ()

  def getBoard: Board = game.board
  def getLastResult: Option[Boolean] = lastResult
  def isFinished: Boolean = game.board.allMatched