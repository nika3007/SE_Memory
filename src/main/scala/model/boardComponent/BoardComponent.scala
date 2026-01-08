package model.boardComponent


import model.Board
import model.boardComponent.boardBaseImpl.BoardImpl

object BoardComponent:
  def apply(board: Board): BoardAPI =
    new BoardImpl(board)
