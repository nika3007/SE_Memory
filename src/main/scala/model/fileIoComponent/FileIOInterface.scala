package model.fileIoComponent

import model.Board

trait FileIOInterface {

  def load: Board
  def save(board: Board): Unit

}
