@main def MemoryGame(rows: Int = 4, cols: Int = 4): Unit =
  require(rows > 0 && cols > 0, "rows and cols must be positive")
  require((rows * cols) % 2 == 0, "rows * cols must be even")

  println(s" Welcome to Memory! ($rows x $cols)\n")

  val symbols = Vector("🍎", "🍇", "🍒", "🍌", "🍉", "🍑", "🍓", "🍍","🥝","🍐","🍊","⭐","❄️","🔥","🎲","🐱","🐶","🐼")
  val needed  = rows * cols / 2
  val pool    = (symbols.take(needed) ++ symbols.take(needed)).toVector
  val deck    = scala.util.Random.shuffle(pool)

  // gib feld aus
  for (r <- 0 until rows)
    println((0 until cols).map(c => s"[${deck(r * cols + c)}]").mkString(" "))

  println("\nAll cards are shuffled and ready!")

//sbt "runMain MemoryGame 6 4"


/*

echo 'export JAVA_HOME=$(/usr/libexec/java_home -v 17)' >> ~/.zshrc
echo 'export SBT_JAVA_HOME="$JAVA_HOME"'               >> ~/.zshrc
echo 'export PATH="$JAVA_HOME/bin:$PATH"'              >> ~/.zshrc

source ~/.zshrc

echo $JAVA_HOME
java -version
sbt sbtVersion
*/