import aview.MemoryTui
import controller.Controller
import model.Card
import model.Board
import model.MemoryGame

@main def runMemory: Unit =

  println(s" Welcome to Memory!")

  val controller = Controller(4, 4)
  val tui = MemoryTui(controller)
  tui.run()

/*

echo 'export JAVA_HOME=$(/usr/libexec/java_home -v 17)' >> ~/.zshrc
echo 'export SBT_JAVA_HOME="$JAVA_HOME"'               >> ~/.zshrc
echo 'export PATH="$JAVA_HOME/bin:$PATH"'              >> ~/.zshrc

source ~/.zshrc

echo $JAVA_HOME
java -version
sbt sbtVersion
*/