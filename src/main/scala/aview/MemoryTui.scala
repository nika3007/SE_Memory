package aview

import controller.Controller
import util.Observer
import controller.controllerComponent.GameStatus
import util.HintSystem
import scala.io.StdIn.readLine
import util.{AsciiRenderer, BoardRenderer}
import controller.controllerComponent.ControllerAPI



private val isTest: Boolean =
  sys.props.contains("test.env")



class MemoryTui(val controller: ControllerAPI) extends Observer:

  controller.add(this)

  //neu templete Rendere:
  private val renderer: BoardRenderer = AsciiRenderer()

  //Testbare Eingabeverarbeitung wie beim Prof
  def processInputLine(input: String): Unit =
    controller.processInput(input)


  def run(): Unit =
    if (isTest) return

    //Start:
    println()
    println(s"🎮 Memory gestartet! Level 1\n")
    //println("👉 Du bist dran!")
    //println(boardToString)
    //println()

    var levelJustStarted = false   // >>> FIX 2

    var playing = true

    while playing do

      //Zeige zu Beginn das Board an:
      //println(boardToString) //alte tui ausgabe
      //println(boardToString) ->neu zu:
      //println(renderer.render(controller.board))
      //println()

      while (!controller.board.allMatched) do
        //println()

        // --- HUMAN TURN -------------------------------------------------
        if controller.currentPlayer == "human" then

          if controller.gameStatus == GameStatus.Idle
              || controller.gameStatus == GameStatus.NextRound then
              //|| controller.gameStatus == GameStatus.Match then
              // Wer beginnt das neue Level?
              println("👉 Du bist dran!")
              println(renderer.render(controller.board))
              println()

          // Erste Karte?
          if controller.gameStatus == GameStatus.Idle
            || controller.gameStatus == GameStatus.NextRound
            || controller.gameStatus == GameStatus.Match then
            println(s"Wähle erste Karte (0 bis ${controller.board.cards.size - 1}):")

          // Zweite Karte?
          else if controller.gameStatus == GameStatus.FirstCard then
            println(s"Wähle zweite Karte (0 bis ${controller.board.cards.size - 1}):")


          val input = readLine()
          var continue = true

          //val input = readLine()

          //HINT SYSTEM --------------------------------------------------
          if input.trim.toLowerCase == "hint" then
            HintSystem.getHint(controller.board) match
              case Some((a, b)) =>
                println(s"💡 Hinweis: Sicheres Paar → Karte $a und Karte $b!")
                println()
              case None =>
                println("💡 Kein sicheres Paar bekannt.")
            //println(boardToString)
            //println(renderer.render(controller.board))
            //println()

            // NICHT als Spielzug werten → also weiter zur nächsten Runde:
            continue = true
          else
            // Normale Eingabe verarbeiten
            continue = controller.processInput(input)


          //Abbruch:
          if !continue then
            println()
            println("Spiel beendet durch Eingabeabbruch. Bye👋")
            println()
            return



        // --- AI TURN ----------------------------------------------------
        else if controller.aiEnabled then

          // >>> FIX 2: Kein "AI ist dran!" direkt nach Levelstart
          if !levelJustStarted then
            println("🤖 AI ist dran!")
            println(renderer.render(controller.board))
            println()


          Thread.sleep(1000)
          println("🤖 AI wählt erste Karte...")
          controller.aiTurnFirst()
          Thread.sleep(1500)

          // zweite Karte kommt NACH observer update
          println("🤖 AI wählt zweite Karte... bitte warten!")
          //println()
          controller.aiTurnSecond()
          Thread.sleep(1000)

          // >>> FIX 2: Nach dem ersten AI-Zug im neuen Level wieder normal drucken
          levelJustStarted = false


      // --- LEVEL DONE -> next Level ---------------------------------------------------
      if controller.game.nextLevel() then
        val lvl = controller.game.currentLevelIndex

        println(s"Next level: $lvl / ${controller.game.levelsCount}")

        // Wer startet dieses Level?
        if controller.currentPlayer == "human" then
          println("👉 Du bist dran!")
        else
          println("🤖 AI startet dieses Level!")

        println(renderer.render(controller.board))
        println()

      else
        // game over
        playing = false
        println()

        if controller.currentPlayer == "human" then
          println("🎉 Du hast das ganze Spiel gewonnen! 🎉")
          println()
        else
          println("🤖 Die AI hat das ganze Spiel gewonnen! 🎉")

        println()


  //Observer-Update-Methode:
  override def update: Boolean =
    if (isTest) then return true

    val msg = GameStatus.message(controller.gameStatus)

    // 1) Meldung immer zuerst
    if msg.nonEmpty then
      println()
      println(msg)
      //println()

    // 2) Bei FirstCard und NextRound das Board NACH der Meldung
    controller.gameStatus match
      case GameStatus.FirstCard =>
        println()
        println(renderer.render(controller.board))
        println()

      case GameStatus.SecondCard =>
        println(renderer.render(controller.board))
        println()

      case GameStatus.Match =>
        println(renderer.render(controller.board))
        println()

      case GameStatus.NoMatch =>
        //println()
        //println(boardToString)
        println(renderer.render(controller.board))
        println()

      case GameStatus.InvalidSelection(i) =>
        //println(boardToString)
        println(renderer.render(controller.board))
        //println()

      case GameStatus.NextRound =>
        println()
        //println(renderer.render(controller.board))

      case GameStatus.Idle =>
        () // nichts drucken

    //controller.gameStatus = GameStatus.Idle //Nach jeder Ausgabe setzt die TUI den Status zurück, verhindert doppelte Nachrichten
    true

  /* wird ersetzt durch renderer
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
    */
