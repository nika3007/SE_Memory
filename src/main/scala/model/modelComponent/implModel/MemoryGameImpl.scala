package model.modelComponent.implModel
import scala.util.Random
import model.*
import model.modelComponent.MemoryGameAPI


final class MemoryGameImpl(
  val theme: Theme,
  val ai: AIPlayer,
  val levels: Vector[Level]
) extends MemoryGameAPI:

  private var _levelIndex: Int = 0
  private var _board: Board = buildBoard(levels(_levelIndex))

  override def board: Board = _board
  override def board_=(b: Board): Unit = _board = b
  override def levelsCount: Int = levels.size

  override def currentLevel: Level = levels(_levelIndex)

  override def currentLevelIndex: Int = _levelIndex
  override def currentLevelNumber: Int = _levelIndex + 1

  override def save(): GameMemento =
    GameMemento(board)

  override def restore(m: GameMemento): Unit =
    board = m.board

  override def nextLevel(): Boolean =
    if _levelIndex + 1 < levels.size then
      _levelIndex += 1
      board = buildBoard(levels(_levelIndex))
      true
    else
      false

  private def buildBoard(level: Level): Board = {
  val size   = level.size
  val needed = (size.rows * size.cols) / level.difficulty.matchAmount

  val symbols =
    LazyList.continually(theme.symbols).flatten.take(needed).toVector

  val deck  = Random.shuffle(symbols ++ symbols)
  val cards = deck.zipWithIndex.map { case (s, i) => Card(i, s) }.toVector

  Board(cards)
}