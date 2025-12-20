package controller.controllerComponent.controllerBaseImpl

import controller.*
import controller.controllerComponent.ControllerAPI
import model.*
import model.modelComponent.MemoryGameAPI
import util.Observable
import scala.util.Try
import controller.controllerComponent.GameStatus


final class ControllerImpl(private val _game: MemoryGameAPI)
  extends Observable
  with ControllerAPI:

  override def game: MemoryGameAPI = _game

  private var _gameStatus: GameStatus = GameStatus.Idle
  override def gameStatus: GameStatus = _gameStatus

  private var _currentPlayer: String = "human"
  override def currentPlayer: String = _currentPlayer

  override def board: Board = game.board

  // command stacks for undo/redo
  private var undoStack: List[Command] = Nil
  private var redoStack: List[Command] = Nil

  override def aiEnabled: Boolean =
    !game.ai.isInstanceOf[NoAI]

  private def execute(cmd: Command): Unit =
    cmd.doStep()
    undoStack = cmd :: undoStack
    redoStack = Nil
    notifyObservers

  override def undo(): Unit = undoStack match
    case cmd :: rest =>
      undoStack = rest
      cmd.undoStep()
      redoStack = cmd :: redoStack
      notifyObservers
    case Nil =>
      println("Nothing to undo")

  override def redo(): Unit = redoStack match
    case cmd :: rest =>
      redoStack = rest
      cmd.doStep()
      undoStack = cmd :: undoStack
      notifyObservers
    case Nil =>
      println("Nothing to redo")

  override def processInput(input: String): Boolean =
    if input == null || input.trim.isEmpty then return false

    if input.trim.equalsIgnoreCase("u") then { undo(); return true }
    if input.trim.equalsIgnoreCase("r") then { redo(); return true }

    // ignore human input when AI is active
    if currentPlayer == "ai" then return true

    Try(input.toInt).toOption match
      case Some(i) if i >= 0 && i < board.cards.size =>
        execute(new ChooseCardCommand(this, i))
        true
      case _ =>
        _gameStatus = GameStatus.InvalidSelection(-1)
        notifyObservers
        true

  // AI uses the same command path
  override def aiTurnFirst(): Unit =
    if currentPlayer != "ai" then return
    execute(new ChooseCardCommand(this, game.ai.chooseCard(board)))

  override def aiTurnSecond(): Unit =
    if currentPlayer != "ai" then return
    execute(new ChooseCardCommand(this, game.ai.chooseCard(board)))

  // make this visible to Command (same package boundary as before)
  private[controllerComponent] def handleCardSelection(i: Int): Unit =
    val oldBoard = board
    val (nextBoard, result) = board.choose(i)

    val invalid = (nextBoard eq oldBoard) && result.isEmpty
    if invalid then
      _gameStatus = GameStatus.InvalidSelection(i)
      return

    game.board = nextBoard

    result match
      case None =>
        _gameStatus = GameStatus.FirstCard

      case Some(true) =>
        if game.board.cards.forall(_.isMatched) then
          _gameStatus = GameStatus.LevelComplete
          game.nextLevel()
          _currentPlayer = "human"
        else
          _gameStatus = GameStatus.Match

      case Some(false) =>
        _gameStatus = GameStatus.NoMatch
        Thread.sleep(1500)
        val resetBoard = board.copy(
          cards = board.cards.map {
            case c if c.isFaceUp && !c.isMatched => c.flip
            case c => c
          }
        )
        game.board = resetBoard
        _gameStatus = GameStatus.NextRound

        _currentPlayer =
          game.ai match
            case _: NoAI => "human"
            case _ => if _currentPlayer == "human" then "ai" else "human"