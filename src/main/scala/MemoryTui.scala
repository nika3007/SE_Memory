import scala.io.StdIn.readLine
import scala.util.Random
import scala.util.Try

class MemoryTui(rows: Int, cols: Int):
  val symbols = Vector("🍎", "🍇", "🍒", "🍌", "🍉", "🍑", "🍓", "🍍", "⭐", "❄️", "🔥", "🎲", "🐱", "🐶", "🐼")
  val needed = rows * cols / 2
  val deck = scala.util.Random.shuffle(symbols.take(needed) ++ symbols.take(needed))
  val cards = deck.zipWithIndex.map { case (s, i) => Card(i, s) }.toVector
  var board = Board(cards)

  def run(): Unit =
    println(s"🎮 Memory gestartet! ($rows x $cols)\n")

    while (!board.allMatched)
      showBoard()
      println("Wähle eine Karte (0 bis " + (cards.size - 1) + "):")
      val input = scala.io.StdIn.readInt()
      val (nextBoard, result) = board.choose(input)
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

    println("🎉 Alle Paare gefunden! Du hast gewonnen!")

  def showBoard(): Unit =
    for (r <- 0 until rows)
      println((0 until cols).map { c =>
        val i = r * cols + c
        val card = board.cards(i)
        if card.isMatched then s"[✅]"
        else if card.isFaceUp then s"[${card.symbol}]"
        else "[ ]"
    }.mkString(" "))