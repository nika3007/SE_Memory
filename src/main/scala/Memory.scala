@main def MemoryGame(rows: Int = 4, cols: Int = 4): Unit =
  require(rows > 0 && cols > 0, "rows and cols must be positive")
  require((rows * cols) % 2 == 0, "rows * cols must be even")

  println(s" Welcome to Memory! ($rows x $cols)\n")

  val tui = new MemoryTui(rows, cols)
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