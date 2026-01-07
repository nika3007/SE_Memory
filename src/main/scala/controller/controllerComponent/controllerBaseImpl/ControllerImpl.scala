package controller.controllerComponent.controllerBaseImpl

import controller.controllerComponent.{ControllerAPI, GameStatus, Command}
import model.*
import model.modelComponent.MemoryGameAPI
import util.Observable

import scala.util.Try

import com.google.inject.Inject

final class ControllerImpl @Inject() (private val _game: MemoryGameAPI)

  extends Observable
  with ControllerAPI:

  override def game: MemoryGameAPI = _game

  private[controllerComponent] var _gameStatus: GameStatus = GameStatus.Idle
  override def gameStatus: GameStatus = _gameStatus

  private[controllerComponent] var _currentPlayer: String = "human"
  override def currentPlayer: String = _currentPlayer

  override def board: Board = game.board

  @volatile private[controllerComponent] var cancelThread = false

  private var undoStack: List[Command] = Nil
  private var redoStack: List[Command] = Nil

  override def aiEnabled: Boolean =
    !game.ai.isInstanceOf[NoAI]

  private def execute(cmd: Command): Unit =
    cancelThread = false
    cmd.doStep()
    undoStack = cmd :: undoStack
    redoStack = Nil

  override def undo(): Unit =
    cancelThread = true
    undoStack match
      case cmd :: rest =>
        undoStack = rest
        cmd.undoStep()
        redoStack = cmd :: redoStack
      case Nil => ()

  override def redo(): Unit =
    redoStack match
      case cmd :: rest =>
        redoStack = rest
        cancelThread = false
        cmd.doStep()
        undoStack = cmd :: undoStack
      case Nil => ()

  override def processInput(input: String): Boolean =
    if input == null || input.trim.isEmpty then return false
    if currentPlayer == "ai" then return true

    Try(input.trim.toInt).toOption match
      case Some(i) if i >= 0 && i < board.cards.size =>
        execute(new ChooseCardCommand(this, i))
        true
      case _ =>
        _gameStatus = GameStatus.InvalidSelection(-1)
        notifyObservers
        true

  override def aiTurnFirst(): Unit =
    if currentPlayer == "ai" then
      execute(new ChooseCardCommand(this, game.ai.chooseCard(board)))

  override def aiTurnSecond(): Unit =
    if currentPlayer == "ai" then
      execute(new ChooseCardCommand(this, game.ai.chooseCard(board)))

  private def advanceLevelIfPossible(): Unit =
    _gameStatus = GameStatus.LevelComplete
    notifyObservers

    val hasNext = game.nextLevel()
    undoStack = Nil
    redoStack = Nil
    _currentPlayer = "human"

    if hasNext then
      _gameStatus = GameStatus.NextRound
      notifyObservers

  private[controllerComponent] def handleCardSelection(i: Int): Unit =
    val oldBoard = board
    val (nextBoard, result) = board.choose(i)

    if nextBoard.eq(oldBoard) && result.isEmpty then
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

        if game.board.cards.forall(_.isMatched) then
          advanceLevelIfPossible()

      case Some(false) =>
        _gameStatus = GameStatus.NoMatch
        notifyObservers

        new Thread(() =>
          Thread.sleep(1200)

          if !cancelThread then
            game.board = game.board.copy(
              cards = game.board.cards.map {
                case c if c.isFaceUp && !c.isMatched => c.flip
                case c => c
              }
            )

          if !cancelThread then
            _currentPlayer =
              if game.ai.isInstanceOf[NoAI] then "human"
              else if _currentPlayer == "human" then "ai" else "human"

          _gameStatus = GameStatus.NextRound
          notifyObservers
        ).start()
