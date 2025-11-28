package model
import scala.util.Random


//Strategy Interface:---
trait AIPlayer:
  def chooseCard(board: Board): Int



//implementierung- MemoryAI:------------------------
class MemoryAI extends AIPlayer:

  private var memory : Map[String, Int] = Map()

  override def chooseCard(board: Board): Int =
    val hidden =
      board.cards.filter(c => !c.isFaceUp && !c.isMatched)

    // 1) Versuche, ein Symbol zu finden, das wir bereits gesehen haben
    val knownMatch: Option[Int] =
      hidden.collectFirst {
        case card if memory.contains(card.symbol) =>
          val id = memory(card.symbol)
          memory -= card.symbol
          id
      }

    knownMatch match
      case Some(cardId) => 
        cardId

      case None =>
        // nichts gefunden → Karte merken
        hidden.foreach(card =>
          memory += (card.symbol -> card.id)
        )

        // und zufällig spielen
        hidden(scala.util.Random.nextInt(hidden.size)).id



//implementierung- RandomAI:---
class RandomAI extends AIPlayer:
  override def chooseCard(board: Board): Int =
    val hidden =
      board.cards.filter(c => !c.isFaceUp && !c.isMatched)

    hidden(Random.nextInt(hidden.size)).id