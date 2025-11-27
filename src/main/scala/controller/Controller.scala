package controller

import model.{MemoryGame, Board, GameMemento}
import util.Observable
import scala.util.Try

class Controller(rows: Int, cols: Int) extends Observable:

  var game: MemoryGame = MemoryGame(rows, cols)
  var gameStatus: GameStatus = GameStatus.Idle // aktueller spielstatus

  // history of executed commands
  private var history: List[Command] = Nil

  def board: Board = game.board

  // central command executor
  private def execute(cmd: Command): Unit =
    cmd.doStep()
    history = cmd :: history
    notifyObservers

  def undo(): Unit = history match
    case cmd :: rest =>
      history = rest
      cmd.undoStep()
      notifyObservers
    case Nil =>
      println("Nothing to undo")

  def processInput(input: String): Boolean =
    // Spiel beenden, wenn Abbruchbedingung
    if input == null || input.trim.isEmpty then

      return false

    // Zahl prüfen
    val inputOpt = Try(input.toInt).toOption

    inputOpt match
      case Some(i) if i >= 0 && i < board.cards.size =>
        // statt direkt handleCardSelection -> Command Pattern
        execute(ChooseCardCommand(this, i))
        true

      case _ =>

        gameStatus = GameStatus.InvalidSelection(-1)
        notifyObservers
        true

  // Spiellogik – nur EINMAL definiert!
  private[controller] def handleCardSelection(i: Int): Unit =
    val oldBoard = board
    val (nextBoard, result) = board.choose(i)

    // Ungültige, wenn schon matched / faceUp / gleiche Karte ---
    val invalid = (nextBoard eq oldBoard) && result.isEmpty

    if invalid then

      gameStatus = GameStatus.InvalidSelection(i)
      notifyObservers
      return

    // Gültige Karte:
    game.board = nextBoard


    result match
      // erste Karte:
      case None =>

        gameStatus = GameStatus.SecondCard

        notifyObservers



      // Match:
      case Some(true) =>
        gameStatus = GameStatus.Match
        notifyObservers


      // Kein Match:
      case Some(false) =>
        gameStatus = GameStatus.NoMatch
        notifyObservers


        Thread.sleep(1500)


        // Karten zurückdrehen
        val resetBoard = board.copy(
          cards = board.cards.map {
            case c if c.isFaceUp && !c.isMatched => c.flip
            case c => c
          }
        )

        game.board = resetBoard
        gameStatus = GameStatus.NextRound
        notifyObservers
