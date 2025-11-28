package model
import scala.util.Random


//Strategy Interface:---
trait AIPlayer:
  def chooseCard(board: Board): Int



//implementierung- MemoryAI:------------------------
class MemoryAI extends AIPlayer:
  
  private var lastSeen: Option[Int] = None

  override def chooseCard(board: Board): Int =
    val hidden =
      board.cards.filter(c => !c.isFaceUp && !c.isMatched)

    // 1) Versuche, ein Symbol zu finden, das wir bereits gesehen haben
    lastSeen match
      case Some(savedId) =>
        lastSeen = None
        // Finde irgendeine versteckte Karte, die NICHT dieselbe ID hat
        hidden.find(_.id != savedId).get.id
        
      case None =>
        // --- 2. Merke diese Karte für später ---
        val choice = hidden.head.id    // deterministisch
        lastSeen = Some(choice)
        choice



//implementierung- RandomAI:---
class RandomAI extends AIPlayer:
  override def chooseCard(board: Board): Int =
    val hidden =
      board.cards.filter(c => !c.isFaceUp && !c.isMatched)

    hidden(Random.nextInt(hidden.size)).id