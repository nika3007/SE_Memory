package model.modelComponent.implModel

import scala.util.Random
import model.*
import model.modelComponent.MemoryGameAPI

final class MemoryGameImpl(
  private var _theme: Theme,
  private var _ai: AIPlayer,
  val levels: Vector[Level]
) extends MemoryGameAPI:

  // Getter für API
  override def theme: Theme = _theme
  override def ai: AIPlayer = _ai

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

  // -----------------------------
  // NEU: Theme dynamisch setzen
  // -----------------------------
  override def setTheme(name: String): Unit =
    _theme = ThemeFactory.getTheme(name)
    // Board neu generieren, damit Theme sofort sichtbar ist
    _board = buildBoard(currentLevel)

  // -----------------------------
  // NEU: AI dynamisch setzen
  // -----------------------------
  override def setAI(name: String): Unit =
    _ai = name.toLowerCase match
      case "none"   => NoAI()
      case "easy"   => RandomAI()
      case "medium" => MediumAI()
      case "hard"   => HardAI()
      case "pro"    => MemoryAI()
      case _        => RandomAI()

  // -----------------------------
  // Board generieren
  // -----------------------------
  private def buildBoard(level: Level): Board =
    val size   = level.size
    val needed = (size.rows * size.cols) / level.difficulty.matchAmount

    val symbols =
      LazyList.continually(_theme.symbols).flatten.take(needed).toVector

    val deck  = Random.shuffle(symbols ++ symbols)
    val cards = deck.zipWithIndex.map { case (s, i) => Card(i, s) }.toVector

    Board(cards)
