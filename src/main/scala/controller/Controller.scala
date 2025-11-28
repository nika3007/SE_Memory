package controller

import model.{MemoryGame, Board, GameMemento}
import util.Observable
import scala.util.Try

class Controller(val game: MemoryGame) extends Observable:

  var gameStatus: GameStatus = GameStatus.Idle // aktueller spielstatus

  // history von ausgeführten commands für undo & redo
  private var history: List[Command] = Nil

  def board: Board = game.board

  // führt commands hauptsächlich aus
  private def execute(cmd: Command): Unit =
    cmd.doStep()
    history = cmd :: history
    //notifyObservers

  def undo(): Unit = history match
    case cmd :: rest =>
      history = rest
      cmd.undoStep()
      notifyObservers
    case Nil =>
      println("Nothing to undo")

  //User Input Verarbeitung:
  def processInput(input: String): Boolean =
    // Spiel beenden, wenn Abbruchbedingung
    if input == null || input.trim.isEmpty then
      return false

    // ➜ NEU: Undo-Befehl
    if input.trim.toLowerCase == "u" then
      undo()
      return true
      

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

  // Spiellogik – nur EINMAL definiert!:
  private[controller] def handleCardSelection(i: Int): Unit =
    val oldBoard = board
    val (nextBoard, result) = board.choose(i)

    // Ungültige Karte, wenn schon matched / faceUp / gleiche Karte ---
    val invalid = (nextBoard eq oldBoard) && result.isEmpty

    if invalid then
      gameStatus = GameStatus.InvalidSelection(i)
      notifyObservers
      return

    // Gültige Karte = Board übernehmen:
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
