package model
import scala.util.Random

final case class MemoryGame(theme: Theme, ai: AIPlayer, levels: Vector[Level])
  extends MemoryGameAPI:

  private var _currentLevelIndex: Int = 0

  // aktuelles Level
  def currentLevel: Level = levels(_currentLevelIndex)
  override def levelsCount: Int = levels.size
  override def currentLevelNumber: Int = _currentLevelIndex + 1


  // aktuelles Board
  var board: Board = buildBoard(currentLevel)

  // Board für ein Level erzeugen
  private def buildBoard(level: Level): Board =
    val size = level.size
    val needed = (size.rows * size.cols) / level.difficulty.matchAmount

    val symbols =
      LazyList.continually(theme.symbols).flatten.take(needed).toVector

    val deck = Random.shuffle(symbols ++ symbols)

    val cards =
      deck.zipWithIndex.map { case (s, i) => Card(i, s) }.toVector

    Board(cards)

//memento für undo
  def save(): GameMemento =
    GameMemento(board)

  def restore(m: GameMemento): Unit =
    this.board = m.board

  override def nextLevel(): Boolean =
    if _currentLevelIndex + 1 < levels.size then
      _currentLevelIndex += 1
      val newBoard = buildBoard(currentLevel)
      board = newBoard.copy(cards = Random.shuffle(newBoard.cards))
      true
    else
      false
