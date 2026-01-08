package aview

import controller.controllerComponent.{ControllerAPI, GameStatus}
import util.{AsciiRenderer, BoardRenderer, Observer, HintSystem}

import scala.io.StdIn.readLine

private val isTest: Boolean =
  sys.props.contains("test.env")

class MemoryTui(val controller: ControllerAPI) extends Observer:

  controller.add(this)

  private val renderer: BoardRenderer = AsciiRenderer()

  private var lastStatus: Option[GameStatus] = None

  private def handleHumanInput(): Unit =
    val input =
      try readLine()
      catch
        case _: Throwable =>
          println("\nSpiel beendet durch Eingabeabbruch. Bye👋\n")
          System.exit(0)
          null

    if input == null || input.trim.isEmpty then
      println("\nSpiel beendet durch Eingabeabbruch. Bye👋\n")
      System.exit(0)

    val trimmed = input.trim.toLowerCase

    if trimmed == "u" || trimmed == "undo" then
      controller.undo()
      return

    if trimmed == "r" || trimmed == "redo" then
      controller.redo()
      return

    if trimmed == "hint" then
      HintSystem.getHint(controller.board) match
        case Some((a, b)) =>
          println(s"💡 Hinweis: Sicheres Paar → Karte $a und Karte $b!\n")
        case None =>
          println("💡 Kein sicheres Paar bekannt.\n")
      return

    controller.processInput(trimmed)

  def run(): Unit =
    if isTest then return

    println()
    println("🎮 Memory gestartet!")
    println()
    println("👉 Du bist dran!")
    println(renderer.render(controller.board))
    println()

    while true do
      controller.currentPlayer match

        case "human" if controller.gameStatus != GameStatus.NoMatch =>
          controller.gameStatus match
            case GameStatus.FirstCard =>
              println(s"Wähle zweite Karte (0 bis ${controller.board.cards.size - 1}):")

            case _ =>
              println(s"Wähle erste Karte (0 bis ${controller.board.cards.size - 1}):")

          handleHumanInput()

        case "ai" =>
          controller.gameStatus match
            case GameStatus.Idle | GameStatus.NextRound | GameStatus.Match =>
              println("🤖 AI ist dran!")
              println(renderer.render(controller.board))
              println()

              Thread.sleep(400)
              println("🤖 AI wählt 1. Karte...\n")
              controller.aiTurnFirst()

              Thread.sleep(700)
              println("🤖 AI wählt 2. Karte...\n")
              controller.aiTurnSecond()

            case _ =>
              Thread.sleep(100)

        case _ =>
          Thread.sleep(100)

  override def update: Boolean =
    if isTest then return true

    val status = controller.gameStatus

    if lastStatus.contains(status) && status != GameStatus.Idle then
      return true

    lastStatus = Some(status)

    val msg = GameStatus.message(status)
    if msg.nonEmpty && status != GameStatus.NextRound then
      println()
      println(msg)

    status match
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

      case GameStatus.Idle =>
        ()

      case _ => ()

    true


  // Für Tests 
  def processInputLine(input: String): Unit =
    val trimmed = input.trim.toLowerCase

    if trimmed == "u" || trimmed == "undo" then
      controller.undo()
      return

    if trimmed == "r" || trimmed == "redo" then
      controller.redo()
      return

    if trimmed == "hint" then
      HintSystem.getHint(controller.board)
      return

    controller.processInput(trimmed)
