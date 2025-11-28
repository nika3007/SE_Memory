package aview

import controller.Controller
import util.Observer
import controller.GameStatus
import scala.io.StdIn.readLine



class MemoryTui(val controller: Controller) extends Observer:

  controller.add(this)

  // NEU: Testbare Eingabeverarbeitung wie beim Prof
  // -----------------------------------------
  def processInputLine(input: String): Unit =
    controller.processInput(input)
  // -----------------------------------------


  def run(): Unit =
    //Start:
    println(s"🎮 Memory gestartet! Level 1\n")

    var playing = true

    while playing do

      //Zeige zu Beginn das Board an:
      println(boardToString)
      //println()

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

      // LEVEL abgeschlossen →
      if controller.game.nextLevel() then
        val next = controller.game.levels.indexOf(controller.game.currentLevel) + 1
        println(s"🎉 Level abgeschlossen! Starte Level $next ...\n")

      else
        // KEIN weiteres Level → fertig!
        playing = false
        println("🎉 Alle Levels abgeschlossen! Du hast das ganze Spiel gewonnen! 🎉")


  //Observer-Update-Methode:
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
    val cards = controller.board.cards
    val total = cards.size

    val levelRows = controller.game.currentLevel.size.rows
    val levelCols = controller.game.currentLevel.size.cols

    // Tests verwenden NICHT die Level-Größe → Test-Boards immer 2 Spalten
    val (rows, cols) =
      if levelRows * levelCols == total then
        // echte Spiel-Level → Levelgröße nehmen
        (levelRows, levelCols)
      else
        // Tests → 2 Spalten, beliebig viele Zeilen
        (total / 2, 2)

    (0 until rows).map { r =>
      (0 until cols).map { c =>
        val i = r * cols + c
        val card = cards(i)

        if card.isMatched then "[✅]"
        else if card.isFaceUp then s"[${card.symbol}]"
        else "[ ]"
      }.mkString(" ")
    }.mkString("\n")
