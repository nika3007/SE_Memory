package model
import scala.util.Random

final case class MemoryGame(
    theme: Theme,
    ai: AIPlayer,
    levels: Vector[Level]
):

  private var currentLevelIndex: Int = 0 //i=0, level1 -> main

  // aktuelles Level
  def currentLevel: Level = levels(currentLevelIndex)

  // aktuelles Board
  var board: Board = buildBoard(currentLevel)

  // Board für ein Level erzeugen
  private def buildBoard(level: Level): Board =
    val size = level.size
    val needed = (size.rows * size.cols) / level.difficulty.matchAmount

    val symbols =
      Stream.continually(theme.symbols).flatten.take(needed).toVector

    val deck =
      scala.util.Random.shuffle(symbols ++ symbols)

    val cards =
      deck.zipWithIndex.map { case (s, i) => Card(i, s) }.toVector

    Board(cards)

  // --- MEMENTO PATTERN (für Undo) -----------------------------

  def save(): GameMemento =
    GameMemento(board)

  def restore(m: GameMemento): Unit =
    this.board = m.board

  // --- LEVEL-STEUERUNG ----------------------------------------

  def nextLevel(): Boolean =
    if currentLevelIndex + 1 < levels.size then
      currentLevelIndex += 1
      board = buildBoard(currentLevel)
      true
    else
      false // keine weiteren Levels
