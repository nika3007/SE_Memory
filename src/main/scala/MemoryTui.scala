import scala.io.StdIn.readLine
import scala.util.Random
import scala.util.Try


class MemoryTui(rows: Int, cols: Int):
  val symbols = Vector("🍎", "🍇", "🍒", "🍌", "🍉", "🍑", "🍓", "🍍", "⭐", "❄️", "🔥", "🎲", "🐱", "🐶", "🐼")
  val needed = rows * cols / 2
  val deck = scala.util.Random.shuffle(symbols.take(needed) ++ symbols.take(needed))
  val cards = deck.zipWithIndex.map { case (s, i) => Card(i, s) }.toVector
  var board = Board(cards)

  def run(): Unit = {
    println(s"🎮 Memory gestartet! ($rows x $cols)\n")

    while (!board.allMatched) {
      showBoard()
      println("Wähle eine Karte (0 bis " + (cards.size - 1) + "):")
      
      val input = scala.io.StdIn.readLine()

      // Abbruch mit ^D, ^Z oder leerer Eingabe
      if input == null || input.trim.isEmpty then
        println("\n Spiel beendet durch Eingabeabbruch. Bye👋")
        return

      // Eingabe prüfen: ist es eine gültige Zahl?
      val inputOpt = Try(input.toInt).toOption //Fehlereingaben auffangen, ohne absturz
      
      //Eingabe prüfen: im Zahlenbereich?
      inputOpt match {
        case Some(i) if i >= 0 && i < board.cards.size => //some(i) bekommt i von Option, wenn gültige zahl
          val (nextBoard, result) = board.choose(i) //karte umdrehen, prüfen, tupel(board, Result zurück geben)
          board = nextBoard

          result match
            case Some(true)  => 
              println("✅ Treffer!")
              //board = nextBoard

            case Some(false) => 
              println("❌ Kein Treffer.")
              //board = nextBoard
              showBoard()
              Thread.sleep(1500) // warte 1.5 Sekunden
              println("nächste Runde...")
              board = board.copy(cards = board.cards.map {
              case c if c.isFaceUp && !c.isMatched => c.flip
              case c => c   
              })

            case None        => 
                  println("zweite Karte wählen...")
                  //board = nextBoard

        case _ =>
            println(s"❗ Ungültige Eingabe. Bitte eine Zahl zwischen 0 und ${board.cards.size - 1} eingeben.")      
        }
    }

    println("Alle Paare gefunden! Du hast gewonnen! 🎉")
  }

  //visuelle Darstellung vom Memory-Spielfeld im Terminal
  def showBoard(): Unit =
    for (r <- 0 until rows) //für jedes r im Bereich 0 bis row-1, until row erreicht
      println((0 until cols).map { c => //„Für jedes Element c aus dem Bereich (0 until cols), führe diesen Codeblock aus.“
        val i = r * cols + c
        val card = board.cards(i)
        if card.isMatched then s"[✅]"
        else if card.isFaceUp then s"[${card.symbol}]"
        else "[ ]"
    }.mkString(" "))