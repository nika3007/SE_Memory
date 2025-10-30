final case class Board(cards: Vector[Card], selection: Option[Int] = None):
  // drehe um, ignoriere Ungültiges
  def flipAt(i: Int): Board =
    if i < 0 || i >= cards.size then this
    else copy(cards = cards.updated(i, cards(i).flip))

  def choose(i: Int): (Board, Option[Boolean]) = // wenn der spieler etwas "ungültiges" tut
    if i < 0 || i >= cards.size || cards(i).isMatched || cards(i).isFaceUp then (this, None)
    //bspw gepaarte karten werden nochmal gepaart, out of range usw.. --> ignoriere und gebe gleiches board wieder

    else selection match //drei möglichkeiten
      case None =>
        (flipAt(i).copy(selection = Some(i)), None) // eine karte gewählt, drehe um & warte auf nächste karte
        // None wenn nichts gewählt wird

      case Some(prev) if prev == i => // gleiche aufgedeckte karte wurde nochmal "aufgedeckt"
        (this, None) // nichts passiert

      case Some(prev) => // zweite karte wird aufgedeckt -> prüfe ob beide gleich sind
        val b2 = flipAt(i)
        val c1 = b2.cards(prev); val c2 = b2.cards(i)
        val isMatch = c1.symbol == c2.symbol // match

        val next = // wenn identisch --> markiere als match und beide bleiben dauerhaft aufgedeckt
          if isMatch then
            b2.copy(
              cards = b2.cards.updated(prev, c1.markMatched).updated(i, c2.markMatched),
              selection = None // selection reinigen für andere karten
            )
          else
            b2.copy( // kein paar & karten werden wieder umgedreht
              cards = b2.cards.updated(prev, b2.cards(prev).flip).updated(i, b2.cards(i).flip),
              selection = None
            )
        (next, Some(isMatch))

  def allMatched: Boolean = cards.forall(_.isMatched)