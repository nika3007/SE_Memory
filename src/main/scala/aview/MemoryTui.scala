package aview

import controller.controllerComponent.{ControllerAPI, GameStatus}
import util.{AsciiRenderer, BoardRenderer, Observer, HintSystem}

import scala.io.StdIn.readLine

private val isTest: Boolean =
  sys.props.contains("test.env")

class MemoryTui(val controller: ControllerAPI) extends Observer:

  controller.add(this)

  private val renderer: BoardRenderer = AsciiRenderer()

  def processInputLine(input: String): Unit =
    controller.processInput(input)

  def run(): Unit =
    if isTest then return

    println()
    println("🎮 Memory gestartet!")
    println("👉 Du bist dran!")
    println(renderer.render(controller.board))
    println()

    while true do
      controller.currentPlayer match

        // ---------------- HUMAN ----------------
        case "human" if controller.gameStatus != GameStatus.NoMatch =>

          controller.gameStatus match
            case GameStatus.Idle | GameStatus.Match | GameStatus.NextRound =>
              println(s"Wähle erste Karte (0 bis ${controller.board.cards.size - 1}):")

            case GameStatus.FirstCard =>
              println(s"Wähle zweite Karte (0 bis ${controller.board.cards.size - 1}):")

            case _ => ()

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

        // ---------------- AI ----------------
        case "ai" =>
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
              Thread.sleep(100)

        case _ =>
          Thread.sleep(100)

  // ---------------- OBSERVER ----------------
  override def update: Boolean =
    if isTest then return true

    val msg = GameStatus.message(controller.gameStatus)
    if msg.nonEmpty then
      println()
      println(msg)

    controller.gameStatus match
      case GameStatus.FirstCard
           | GameStatus.Match
           | GameStatus.NoMatch =>
        println(renderer.render(controller.board))
        println()

      case GameStatus.NextRound =>
        println("nächste Runde...")
        println(renderer.render(controller.board))
        println()

      case GameStatus.LevelComplete =>
        println()
        println("✅ Level complete! Next level...\n")
        println(renderer.render(controller.board))
        println()

      case _ => ()

    true
