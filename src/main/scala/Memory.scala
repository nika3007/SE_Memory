import aview.MemoryTui
import controller.Controller
import model._
import scala.io.StdIn.readLine

@main def runMemory(): Unit =
  println()
  println("Welcome to Memory!")

  println("Choose theme: fruits / animals / emoji / sports / vehicles / flags / landscape")
  val themeName = readLine().trim()
  
  println("Choose AI level: none / easy / medium / hard / pro")
  val aiChoice = readLine().trim.toLowerCase

  // 1) Theme wählen
  val theme = ThemeFactory.getTheme(themeName)

  // 2) KI auswählen
  val ai: AIPlayer = aiChoice match
    case "none"   => NoAI()
    case "easy"   => RandomAI()
    case "medium" => MediumAI()
    case "hard"   => HardAI()
    case "pro"    => MemoryAI()   
    case _        => RandomAI()

  // 3) Levels definieren
  val levels = Vector(
    Level(BoardSizes.Small2x2, Difficulties.Easy), //i=0, level1
    //Level(BoardSizes.Small2x2, Difficulties.Hard),
    Level(BoardSizes.Medium4x4, Difficulties.Easy),
    Level(BoardSizes.Medium4x4, Difficulties.Hard, 0),
    Level(BoardSizes.Large6x6, Difficulties.Easy, 0),
    Level(BoardSizes.Large6x6, Difficulties.Easy, 240),
  )

  // 4) MemoryGame mit Levelsystem erzeugen
  val game = MemoryGame(theme, ai, levels)

  // 5) Controller erzeugen
  val controller = Controller(game)

  // 6) TUI starten
  val tui = MemoryTui(controller)
  tui.run()




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