package aview

import controller.Controller
import util.Observer
import controller.GameStatus
import scala.io.StdIn.readLine

class MemoryTui(controller: Controller) extends Observer:

  controller.add(this)

  // NEU: Testbare Eingabeverarbeitung wie beim Prof
  // -----------------------------------------
  def processInputLine(input: String): Unit =
    controller.processInput(input)
  // -----------------------------------------


  def run(): Unit =
    //Start:
    println(s"🎮 Memory gestartet! (${controller.game.rows} x ${controller.game.cols})\n")

    //Zeige zu Beginn das Board an:
    controller.notifyObservers

    while (!controller.board.allMatched) do
      println()
      println(s"Wähle eine Karte (0 bis ${controller.board.cards.size - 1}):")
      val input = readLine()
      println()
      val continue = controller.processInput(input)
      

      //Abbruch:
      if !continue then 
        println("Spiel beendet durch Eingabeabbruch. Bye👋")
        println()
        return   // <<< HARTE ABBRUCH-KONTROLLE

    println("Alle Paare gefunden! Du hast gewonnen! 🎉")

  override def update: Boolean =
    val msg = GameStatus.message(controller.gameStatus)

    // 1) Meldung immer zuerst
    if msg.nonEmpty then
      println(msg)
      //println()

    // 2) Bei FirstCard und NextRound das Board NACH der Meldung
    controller.gameStatus match
      case GameStatus.SecondCard =>
        println(boardToString)
        //println()
      case GameStatus.NextRound =>
        println(boardToString)
        //println()
      case GameStatus.Match =>
        println(boardToString)
        //println()
      case GameStatus.NoMatch =>
        println(boardToString)
        println()
      case GameStatus.InvalidSelection(i) =>
        println(boardToString)
        //println()
      case GameStatus.Idle =>
        println(boardToString)
        //println()

    controller.gameStatus = GameStatus.Idle //Nach jeder Ausgabe setzt die TUI den Status zurück, verhindert doppelte Nachrichten
    true

  def boardToString: String =
    val rows = controller.game.rows
    val cols = controller.game.cols
    val b = controller.board

    (0 until rows).map { r =>
      (0 until cols).map { c =>
        val i = r * cols + c
        val card = b.cards(i)

        if card.isMatched then "[✅]"
        else if card.isFaceUp then s"[${card.symbol}]"
        else "[ ]"
      }.mkString(" ")
    }.mkString("\n")
