package controller

import model.MemoryGame
import model.Board
import util.Observable
import scala.util.Try

class Controller(rows: Int, cols: Int) extends Observable:

  var game: MemoryGame = MemoryGame(rows, cols)
  var gameStatus: GameStatus = GameStatus.Idle // aktueller spielstatus

  def board: Board = game.board

  def processInput(input: String): Boolean =
    // Spiel beenden, wenn Abbruchbedingung
    if input == null || input.trim.isEmpty then
      //gameStatus = GameStatus.Idle
      //notifyObservers()
      //println("Spiel beendet durch Eingabeabbruch. Bye👋")
      return false

    // Zahl prüfen
    val inputOpt = Try(input.toInt).toOption

    inputOpt match
      case Some(i) if i >= 0 && i < board.cards.size =>
        handleCardSelection(i)
        true

      case _ =>
        //println(s"❗ Ungültige Eingabe. Bitte Zahl zwischen 0 und ${board.cards.size - 1}.")
        gameStatus = GameStatus.InvalidSelection(-1)
        notifyObservers
        true



  // Spiellogik:

  private def handleCardSelection(i: Int): Unit =
    val oldBoard = board
    val (nextBoard, result) = board.choose(i)

    // Ungültige, wenn schon matched / faceUp / gleiche Karte ---
    val invalid = (nextBoard eq oldBoard) && result.isEmpty

    if invalid then
      //println(s"❗ Karte $i kann nicht gewählt werden (bereits offen oder matched).\n")
      gameStatus = GameStatus.InvalidSelection(i)
      notifyObservers
      return

    // Gültige Karte:
    game.board = nextBoard


    result match
      //erste Karte:
      case None =>
        gameStatus = GameStatus.SecondCard
        // Erst Board anzeigen
        notifyObservers

        // Dann Meldung unter Board
        //println()
        //println("zweite Karte wählen...")
        //notifyObservers()

      // Match:
      case Some(true) =>
        gameStatus = GameStatus.Match
        notifyObservers
        //println("✅ Treffer!\n")

      // Kein Match:
      case Some(false) =>
        gameStatus = GameStatus.NoMatch
        notifyObservers
        //println("❌ Kein Treffer.\n")

        Thread.sleep(1500)
        //println()
        //println("nächste Runde...\n")

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
