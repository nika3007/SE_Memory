package model.boardComponent

import model.Board

trait BoardAPI:

  // monentanes board zwischenstand
  def board: Board

  // momentaner board zwischenstand wird gespeichert
  def board_=(b: Board): Unit

  def choose(i: Int): (Board, Option[Boolean])

  // wird true wenn alle karten korrekt aufgedeckt wurden
  def allMatched: Boolean

  def cards = board.cards
