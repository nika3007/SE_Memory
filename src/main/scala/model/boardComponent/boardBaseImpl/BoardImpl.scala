package model.boardComponent.boardBaseImpl

import model.boardComponent.BoardAPI
import model.Board

final class BoardImpl(private var _board: Board) extends BoardAPI:

  override def board: Board = _board

  override def board_=(b: Board): Unit =
    _board = b

  override def choose(i: Int): (Board, Option[Boolean]) =
    val (next, res) = _board.choose(i)
    _board = next
    (next, res)

  override def allMatched: Boolean =

    _board.cards.forall(_.isMatched)