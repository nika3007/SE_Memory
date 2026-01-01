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
    println("🎮 Memory gestartet! Level 1\n")
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
              println(s"Wähle eine Karte (0 bis ${controller.board.cards.size - 1}):")

          val input = readLine()
          if input == null then
            println("\nSpiel beendet durch Eingabeabbruch. Bye👋\n")
            return

          val trimmed = input.trim.toLowerCase

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
              controller.aiTurnFirst()

              Thread.sleep(700)
              controller.aiTurnSecond()

            case _ =>
              Thread.sleep(150)

        else
          Thread.sleep(150)

      if controller.board.allMatched then
        playing = false
        println()
        println("🎉 Du hast das ganze Spiel gewonnen! 🎉")
        println()

  // ----------------------------
  // EINZIGER FIX IST HIER
  // ----------------------------
  override def update: Boolean =
    if isTest then return true

    val msg = GameStatus.message(controller.gameStatus)
    if msg.nonEmpty then
      println()
      println(msg)

    controller.gameStatus match
      case GameStatus.FirstCard
           | GameStatus.Match
           | GameStatus.NoMatch
           | GameStatus.NextRound
           | GameStatus.LevelComplete
           | GameStatus.InvalidSelection(_) =>

        println(renderer.render(controller.board))
        println()

        if controller.gameStatus == GameStatus.NextRound then
          println("👉 Du bist dran!")

      case _ =>
        ()

    true
