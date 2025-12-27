package controller.controllerComponent.controllerBaseImpl

import controller.*
import controller.controllerComponent.{ControllerAPI, GameStatus, Command}
import model.*
import model.modelComponent.MemoryGameAPI
import util.Observable
import scala.util.Try
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, ExecutionContext}
import scala.concurrent.ExecutionContext.Implicits.global

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
  private var aiBusy = false

  override def aiEnabled: Boolean =
    !game.ai.isInstanceOf[NoAI]

  private def maybeRunAITurn(): Unit =
    if aiEnabled && _currentPlayer == "ai" && !aiBusy then
      aiBusy = true
      Future {
        Thread.sleep(400)
        execute(new ChooseCardCommand(this, game.ai.chooseCard(board)))
        Thread.sleep(700)
        execute(new ChooseCardCommand(this, game.ai.chooseCard(board)))
        aiBusy = false
      }

  private def execute(cmd: Command): Unit =
    cmd.doStep()
    undoStack = cmd :: undoStack
    redoStack = Nil
    // do NOT notify here; the command/controller will notify at the right times

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

  override def aiTurnFirst(): Unit =
    if currentPlayer != "ai" then return
    execute(new ChooseCardCommand(this, game.ai.chooseCard(board)))

  override def aiTurnSecond(): Unit =
    if currentPlayer != "ai" then return
    execute(new ChooseCardCommand(this, game.ai.chooseCard(board)))


  private[controllerComponent] def handleCardSelection(i: Int): Unit =
    val oldBoard = board
    val (nextBoard, result) = board.choose(i)

    val invalid = nextBoard.eq(oldBoard) && result.isEmpty
    if invalid then
      _gameStatus = GameStatus.InvalidSelection(i)
      notifyObservers
      return

    game.board = nextBoard

    result match
      case None =>
        _gameStatus = GameStatus.FirstCard
        notifyObservers

      case Some(true) =>
  _gameStatus = GameStatus.Match
  notifyObservers
  maybeRunAITurn()

  if game.board.cards.forall(_.isMatched) then
    _gameStatus = GameStatus.LevelComplete
    notifyObservers

    val hasNext = game.nextLevel()

    if hasNext then
      _gameStatus = GameStatus.NextRound
      _currentPlayer = "human"
      notifyObservers
    else
      _currentPlayer = "human"
      notifyObservers
      maybeRunAITurn()
