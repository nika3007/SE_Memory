final case class Card(id: Int, symbol: String, isFaceUp: Boolean = false, isMatched: Boolean = false):
  def flip: Card        = copy(isFaceUp = !isFaceUp) // umdrehen von karten
  def markMatched: Card = copy(isMatched = true, isFaceUp = true) // paare finden -> wenn match existiert und wenn karte aufgedeckt ist

// karte ist weder gepaart noch augedeckt, per default sind diese "versteckt" und haben keine matches
