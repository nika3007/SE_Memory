package model.modelComponent.implModel

import scala.util.Random
import model.*
import model.modelComponent.MemoryGameAPI
import model.boardComponent.{BoardAPI, BoardComponent}

final class MemoryGameImpl(
  private var _theme: Theme,
  private var _ai: AIPlayer,
  val levels: Vector[Level]
) extends MemoryGameAPI:

  // Getter für API
  override def theme: Theme = _theme
  override def ai: AIPlayer = _ai

  private var _levelIndex: Int = 0

  // BoardComponent kapselt model.Board
  private var _board: BoardAPI =
    BoardComponent(buildBoard(levels(_levelIndex)))

  override def board: BoardAPI = _board

  override def levelsCount: Int = levels.size

  override def currentLevel: Level = levels(_levelIndex)

  override def currentLevelIndex: Int = _levelIndex
  override def currentLevelNumber: Int = _levelIndex + 1


  override def save(): GameMemento =
    GameMemento(_board.board)

  
  override def restore(m: GameMemento): Unit =
    _board = BoardComponent(m.board)

  override def nextLevel(): Boolean =
    if _levelIndex + 1 < levels.size then
      _levelIndex += 1
      _board = BoardComponent(buildBoard(levels(_levelIndex)))
      true
    else
      false

 
  // Theme dynamisch setzen
  override def setTheme(name: String): Unit =
    _theme = ThemeFactory.getTheme(name)
    _board = BoardComponent(buildBoard(currentLevel))


  // AI dynamisch setzen
  override def setAI(name: String): Unit =
    _ai = name.toLowerCase match
      case "none"   => NoAI()
      case "easy"   => RandomAI()
      case "medium" => MediumAI()
      case "hard"   => HardAI()
      case "pro"    => MemoryAI()
      case _        => RandomAI()


  // Board generieren (STATE)
  private def buildBoard(level: Level): Board =
    val size   = level.size
    val needed = (size.rows * size.cols) / level.difficulty.matchAmount

    val symbols =
      LazyList.continually(_theme.symbols).flatten.take(needed).toVector

    val deck  = Random.shuffle(symbols ++ symbols)
    val cards = deck.zipWithIndex.map { case (s, i) => Card(i, s) }.toVector

    Board(cards)
