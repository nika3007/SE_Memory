import aview.MemoryTui
import controller.Controller
import model.Card
import model.Board
import model.MemoryGame

import scala.io.StdIn.readLine


@main def runMemory(): Unit =    
    println(s" Welcome to Memory!")

    val controller = Controller(4, 4)
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