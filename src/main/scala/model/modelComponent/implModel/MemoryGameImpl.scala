package model.modelComponent.implModel

import model.*
import scala.util.Random

final class MemoryGameImpl(
  val theme: Theme,
  val ai: AIPlayer,
  val levels: Vector[Level]
) extends MemoryGameAPI:

  private var _levelIndex: Int = 0

  override def levelsCount: Int = levels.size

  override def currentLevelNumber: Int = _levelIndex + 1

  override def board: Board = _board

  override def nextLevel(): Boolean =
    if _levelIndex + 1 < levels.size then
      _levelIndex += 1
      _board = buildBoard(levels(_levelIndex))
      true
    else
      false

  private var _board: Board = buildBoard(levels(_levelIndex))

  private def buildBoard(level: Level): Board =
    Board(Vector.empty)

  def save(): GameMemento = GameMemento(_board)
  def restore(m: GameMemento): Unit = _board = m.board