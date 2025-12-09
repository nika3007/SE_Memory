package util

import model.Board

object HintSystem:  // SINGLETON

  //Gibt IMMER ein echtes Paar zurück, wenn eines existiert.
  def getHint(board: Board): Option[(Int, Int)] =
    // symbol → Liste der Positionen
    val map = scala.collection.mutable.Map[String, List[Int]]()

    // alle Karten sammeln
    for ((card, i) <- board.cards.zipWithIndex) do
      if !card.isMatched then
        map.update(card.symbol, i :: map.getOrElse(card.symbol, Nil))


    // erstes Paar zurückgeben

    map.collectFirst {
      case (_, indices) if indices.size >= 2 =>
        (indices.head, indices.tail.head)
    }
