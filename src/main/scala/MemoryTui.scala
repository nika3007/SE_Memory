import scala.io.StdIn.readLine
import scala.util.Random

@main def MemoryTUI(): Unit =
  println("=== Welcome to Memory (Text UI) ===")
  print("Enter your name: ")
  val name = readLine()
  println(s"Hello, $name!\n")

  val rows = 2
  val cols = 4
  val deck = createDeck(rows, cols)            // erzeugt Karten – unabhängig vom MemoryGame-Code
  var board = Board(deck, selection = None)

  // 🔹 Spielfeld einmal am Anfang anzeigen
  printBoard(board, rows, cols)

  // --- Hauptspielschleife ---
  while !board.allMatched do
    println(s"Choose a card index (0–${board.cards.size - 1}):")
    val input = readLine()

    if input.forall(_.isDigit) then
      val idx = input.toInt
      val (newBoard, result) = board.choose(idx)
      board = newBoard

      result match
        // -------------------------
        // Erste Karte aufgedeckt
        // -------------------------
        case None =>
          printBoard(board, rows, cols)
          println("(choose another card)")

        // -------------------------
        // Zweite Karte – Treffer
        // -------------------------
        case Some(true) =>
          printBoard(board, rows, cols)
          println("✅ Match found!")

        // -------------------------
        // Zweite Karte – kein Treffer
        // -------------------------
        case Some(false) =>
          printBoard(board, rows, cols)
          println("❌ Not a match! (press ENTER to continue)")
          readLine()
          // falsche Karten wieder verdecken
          board = Board(
            board.cards.map { c =>
              if c.isFaceUp && !c.isMatched then c.flip else c
            },
            selection = None
          )
    else
      println("Invalid input.")

  println("\n🎉 All cards matched! You win!")

// ------------------------------------------------------
// Hilfsfunktion zum Erstellen des Decks (lokal hier, MemoryGame bleibt unverändert)
// ------------------------------------------------------

def createDeck(rows: Int, cols: Int): Vector[Card] =
  val symbols = Vector(
    "🍎","🍇","🍒","🍌","🍉","🍑",
    "🍓","🍍","🥝","🍐","🍊","⭐",
    "❄️","🔥","🎲","🐱","🐶","🐼"
  )
  val needed  = (rows * cols) / 2
  val pool    = (symbols.take(needed) ++ symbols.take(needed)).toVector
  Random.shuffle(pool.zipWithIndex.map((sym, i) => Card(i, sym)))

// ------------------------------------------------------
// Spielfeld-Ausgabe mit gleichmäßiger Formatierung (wie MemoryGame)
// ------------------------------------------------------

def printBoard(board: Board, rows: Int, cols: Int): Unit =
  println("\nCurrent Board:")
  for (r <- 0 until rows) do
    val row = for (c <- 0 until cols) yield
      val idx = r * cols + c
      val card = board.cards(idx)
      if card.isMatched then "[✔]"
      else if card.isFaceUp then f"[${card.symbol}%-2s]"  // gleiche Breite für alle Symbole
      else "[❓]"
    println(row.mkString(" "))
