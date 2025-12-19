import scalafx.application.Platform

import aview.MemoryTui
import aview.GUI
import controller.Controller
import model._
import scala.io.StdIn.readLine
import controller.controllerComponent.ControllerAPI
import controller.controllerComponent.controllerBaseImpl.ControllerImpl
import model.modelComponent.implModel.MemoryGameImpl

@main def runMemory(): Unit =
  println()
  println("Welcome to Memory!")

  // 1) Modus auswählen
  println("choose the mode:")
  println("  1) just TUI")
  println("  2) just GUI")
  println("  3) both (TUI + GUI parallel)")
  print("\nyour choice (1-3): ")

  val modeInput = readLine().trim()
  val mode = if (modeInput.nonEmpty) modeInput else "1"


  // 2) Theme wählen
  println("Choose theme: fruits / animals / emoji / sports / vehicles / flags / landscape")
  val themeName = readLine().trim()
  val theme = ThemeFactory.getTheme(if themeName.nonEmpty then themeName else "fruits")


  // 3) KI auswählen
  println("Choose AI level: none / easy / medium / hard / pro")
  val aiChoice = readLine().trim.toLowerCase
  val ai: AIPlayer = aiChoice match
    case "none"   => NoAI()
    case "easy"   => RandomAI()
    case "medium" => MediumAI()
    case "hard"   => HardAI()
    case "pro"    => MemoryAI()
    case _        => RandomAI()


  // 4) Levels definieren
  val levels = Vector(
    Level(BoardSizes.Small2x2, Difficulties.Easy), //i=0, level1
    //Level(BoardSizes.Small2x2, Difficulties.Hard),
    Level(BoardSizes.Medium4x4, Difficulties.Easy),
    Level(BoardSizes.Medium4x4, Difficulties.Hard, 0),
    Level(BoardSizes.Large6x6, Difficulties.Easy, 0),
    Level(BoardSizes.Large6x6, Difficulties.Easy, 240),
  )

  // 5) MemoryGame mit Levelsystem erzeugen
  val game = MemoryGame(theme, ai, levels)

  // 6) Controller erzeugen
  val controller = Controller(game)


  // Je nach Modus starten
  mode match
    case "2" | "gui" =>
      // ========== NUR GUI ==========
      println("\nStarte grafische Oberfläche...")

      // Wichtig: JavaFX Toolkit initialisieren
      Platform.startup(() => {
        println("JavaFX Toolkit initialisiert")
      })

      // GUI erstellen und starten
      val gui = GUI(controller)
      gui.main(Array.empty[String])

    case "3" | "both" =>
      // ========== BEIDE (TUI + GUI) ==========
      println("\nStarte beide Oberflächen parallel...")

      // GUI in eigenem Thread starten
      new Thread(() => {
        Platform.startup(() => {}) // JavaFX initialisieren
        val gui = GUI(controller)
        gui.main(Array.empty[String])
      }).start()

      // Kurz warten, damit GUI starten kann
      Thread.sleep(2000)

      // TUI im Hauptthread starten
      println("\n=== TUI GESTARTET ===")
      println("(Die GUI läuft im Hintergrund)")
      println("Tipp: Gib 'hint' für einen Tipp ein")
      println("      Gib 'u' für Undo ein")
      println()

      val tui = MemoryTui(controller)
      tui.run()

    case _ =>
      // ========== NUR TUI (Default) ==========
      println("\n=== TUI GESTARTET ===")
      println("Tipp: Gib 'hint' für einen Tipp ein")
      println("      Gib 'u' für Undo ein")
      println()

      val tui = MemoryTui(controller)
      tui.run()

end runMemory

  /* 6) TUI starten
  val tui = MemoryTui(controller)
  tui.run()
  */

  // TUI starten (läuft blockierend)
  //val tui = MemoryTui(controller)
  //new Thread(() => tui.run()).start()

  // GUI starten (JavaFX-Thread)
  //GUI(controller).main(Array())

  /*
  val gui = GUI(controller)
  new Thread(() => gui.main(args)).start()
  val tui = MemoryTui(controller)
  tui.run()
  */



/* Main wie beim Prof, für spec mit testbaren objekt, aber man muss dann tui und co anpassen
object Memory:
  def main(args: Array[String]): Unit =
    // Keine Eingabe → nur starten, NICHT readLine aufrufen
    if args.nonEmpty then
      println("Welcome to Memory!")
      val controller = Controller(4, 4)
      val tui = MemoryTui(controller)
      tui.processInputLine(args(0))
    else
      println("Welcome to Memory!")
*/



/*
echo 'export JAVA_HOME=$(/usr/libexec/java_home -v 17)' >> ~/.zshrc
echo 'export SBT_JAVA_HOME="$JAVA_HOME"'               >> ~/.zshrc
echo 'export PATH="$JAVA_HOME/bin:$PATH"'              >> ~/.zshrc

source ~/.zshrc

echo $JAVA_HOME
java -version
sbt sbtVersion
*/