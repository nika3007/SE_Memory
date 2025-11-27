package model

case class MemoryGame(rows: Int, cols: Int):
  require(rows > 0 && cols > 0, "rows and cols must be positive")
  require((rows * cols) % 2 == 0, "rows * cols must be even")

  val symbols = Vector("🍎", "🍇", "🍒", "🍌", "🍉", "🍑", "🍓", "🍍", "⭐", "❄️", "🔥", "🎲", "🐱", "🐶", "🐼")
  val needed  = rows * cols / 2
  private val deck =
    scala.util.Random.shuffle(symbols.take(needed) ++ symbols.take(needed))

  val cards = deck.zipWithIndex.map { case (s, i) => Card(i, s) }.toVector
  var board: Board = Board(cards)

  def save(): GameMemento =
    GameMemento(board)

  def restore(m: GameMemento): Unit =
    this.board = m.board
