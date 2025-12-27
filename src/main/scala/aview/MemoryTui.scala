package aview

import controller.controllerComponent.{ControllerAPI, GameStatus}
import util.Observer
import util.HintSystem
import util.{AsciiRenderer, BoardRenderer}

import scala.io.StdIn.readLine

private val isTest: Boolean =
  sys.props.contains("test.env")

class MemoryTui(val controller: ControllerAPI) extends Observer:

  controller.add(this)

  private val renderer: BoardRenderer = AsciiRenderer()

  // Test-friendly input
  def processInputLine(input: String): Unit =
    controller.processInput(input)

  def run(): Unit =
    if isTest then return

    println()
    println(s"🎮 Memory gestartet! Level 1\n")
    println("👉 Du bist dran!")
    println(renderer.render(controller.board))
    println()

    var playing = true

    while playing do



      while !controller.board.allMatched do

        if controller.currentPlayer == "human" then
          controller.gameStatus match
            case GameStatus.Idle | GameStatus.NextRound | GameStatus.Match =>
              println(s"Wähle erste Karte (0 bis ${controller.board.cards.size - 1}):")

            case GameStatus.FirstCard =>
              println(s"Wähle zweite Karte (0 bis ${controller.board.cards.size - 1}):")

            case _ =>
              // fallback: treat as first card prompt
              println(s"Wähle eine Karte (0 bis ${controller.board.cards.size - 1}):")

          val input = readLine()
          if input == null then
            println("\nSpiel beendet durch Eingabeabbruch. Bye👋\n")
            return

          val trimmed = input.trim.toLowerCase

          // hint is not a move
          if trimmed == "hint" then
            HintSystem.getHint(controller.board) match
              case Some((a, b)) =>
                println(s"💡 Hinweis: Sicheres Paar → Karte $a und Karte $b!\n")
              case None =>
                println("💡 Kein sicheres Paar bekannt.\n")
          else
            val cont = controller.processInput(input)
            if !cont then
              println("\nSpiel beendet durch Eingabeabbruch. Bye👋\n")
              return

        else if controller.aiEnabled then
          controller.gameStatus match
            case GameStatus.Idle | GameStatus.NextRound | GameStatus.Match =>
              println("🤖 AI ist dran!")
              println(renderer.render(controller.board))
              println()

              Thread.sleep(400)
              println("🤖 AI wählt erste Karte...")
              controller.aiTurnFirst()

              Thread.sleep(700)
              println("🤖 AI wählt zweite Karte... bitte warten!")
              controller.aiTurnSecond()

              Thread.sleep(300)

            case _ =>
              Thread.sleep(150)

        else

          Thread.sleep(150)

      if controller.board.allMatched then
        playing = false
        println()
        println("🎉 Du hast das ganze Spiel gewonnen! 🎉")
        println()




  override def update: Boolean =
    if isTest then return true

    val msg = GameStatus.message(controller.gameStatus)
    if msg.nonEmpty then
      println()
      println(msg)

    controller.gameStatus match
      case GameStatus.FirstCard =>
        println()
        println(renderer.render(controller.board))
        println()

      case GameStatus.Match =>
        println(renderer.render(controller.board))
        println()

      case GameStatus.NoMatch =>
        println(renderer.render(controller.board))
        println()

      case GameStatus.InvalidSelection(_) =>
        println(renderer.render(controller.board))
        println()

      case GameStatus.NextRound =>

        println()
        if controller.currentPlayer == "human" then
          println("👉 Du bist dran!")
        else
          println("🤖 AI ist dran!")
        println(renderer.render(controller.board))
        println()

      case GameStatus.LevelComplete =>
        // controller will notify again with NextRound if a next level exists
        println()
        println("✅ Level complete! Next level...\n")

      case GameStatus.Idle =>
        () // nothing

      // if your enum includes SecondCard but you don't use it, safe to ignore
      case _ =>
        ()

    true
