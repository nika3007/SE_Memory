package controller.controllerComponent.controllerBaseImpl

import controller.controllerComponent.{ControllerAPI, GameStatus, Command}
import model.*
import model.modelComponent.MemoryGameAPI
import util.Observable

import scala.util.Try

final class ControllerImpl(private val _game: MemoryGameAPI)
  extends Observable
  with ControllerAPI:

  override def game: MemoryGameAPI = _game

  private var _gameStatus: GameStatus = GameStatus.Idle
  override def gameStatus: GameStatus = _gameStatus

  private var _currentPlayer: String = "human"
  override def currentPlayer: String = _currentPlayer

  override def board: Board = game.board

  private var undoStack: List[Command] = Nil
  private var redoStack: List[Command] = Nil

  override def aiEnabled: Boolean =
    !game.ai.isInstanceOf[NoAI]

  private def execute(cmd: Command): Unit =
    cmd.doStep()
    undoStack = cmd :: undoStack
    redoStack = Nil
    // do NOT notify here; we notify inside handleCardSelection at the right moments

  override def undo(): Unit =
    undoStack match
      case cmd :: rest =>
        undoStack = rest
        cmd.undoStep()
        redoStack = cmd :: redoStack
        notifyObservers
      case Nil =>
        println("Nothing to undo")

  override def redo(): Unit =
    redoStack match
      case cmd :: rest =>
        redoStack = rest
        cmd.doStep()
        undoStack = cmd :: undoStack
        notifyObservers
      case Nil =>
        println("Nothing to redo")

  override def processInput(input: String): Boolean =
    if input == null || input.trim.isEmpty then return false

    val trimmed = input.trim

    if trimmed.equalsIgnoreCase("u") then
      undo()
      return true

    if trimmed.equalsIgnoreCase("r") then
      redo()
      return true

    // ignore human input when AI is active
    if currentPlayer == "ai" then return true

    Try(trimmed.toInt).toOption match
      case Some(i) if i >= 0 && i < board.cards.size =>
        execute(new ChooseCardCommand(this, i))
        true
      case _ =>
        _gameStatus = GameStatus.InvalidSelection(-1)
        notifyObservers
        true

  // AI uses the same command path; the TUI may call these
  override def aiTurnFirst(): Unit =
    if currentPlayer != "ai" then return
    execute(new ChooseCardCommand(this, game.ai.chooseCard(board)))

  override def aiTurnSecond(): Unit =
    if currentPlayer != "ai" then return
    execute(new ChooseCardCommand(this, game.ai.chooseCard(board)))

  private def switchPlayerAfterMismatch(): Unit =
    _currentPlayer =
      game.ai match
        case _: NoAI => "human"
        case _ => if _currentPlayer == "human" then "ai" else "human"

  private def advanceLevelIfPossible(): Unit =
    // show LevelComplete first (observers can print it)
    _gameStatus = GameStatus.LevelComplete
    notifyObservers

    // try to advance
    val hasNext = game.nextLevel()

    if hasNext then
      // prevent "redo/undo into last level"
      undoStack = Nil
      redoStack = Nil

      // new level should accept input again
      _currentPlayer = "human"
      _gameStatus = GameStatus.NextRound
      notifyObservers
    else
      // game finished on last level
      _currentPlayer = "human"
      // keep LevelComplete; views can interpret this as "game won"
      _gameStatus = GameStatus.LevelComplete
      notifyObservers

  // visible to ChooseCardCommand (component boundary)
  private[controllerComponent] def handleCardSelection(i: Int): Unit =
    val oldBoard = board
    val (nextBoard, result) = board.choose(i)

    val invalid = nextBoard.eq(oldBoard) && result.isEmpty
    if invalid then
      _gameStatus = GameStatus.InvalidSelection(i)
      notifyObservers
      return

    // apply the new board state (card flipped)
    game.board = nextBoard

    result match
      case None =>
        // first card flipped
        _gameStatus = GameStatus.FirstCard
        notifyObservers

      case Some(true) =>
        // second card flipped + match
        _gameStatus = GameStatus.Match
        notifyObservers

        // if level completed, controller advances level (works for GUI AND TUI)
        if game.board.cards.forall(_.isMatched) then
          advanceLevelIfPossible()

      case Some(false) =>
        // second card flipped + no match -> show it FIRST
        _gameStatus = GameStatus.NoMatch
        notifyObservers

        Thread.sleep(1500)

        // flip back down
        val resetBoard = game.board.copy(
          cards = game.board.cards.map {
            case c if c.isFaceUp && !c.isMatched => c.flip
            case c => c
          }
        )
        game.board = resetBoard

        _gameStatus = GameStatus.NextRound
        switchPlayerAfterMismatch()
        notifyObservers
