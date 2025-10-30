import org.scalatest.wordspec.AnyWordSpec // ermöglicht specs
import org.scalatest.matchers.should.Matchers

final class BoardSpec extends AnyWordSpec with Matchers {

  "A Memory Board" should { // was soll dieses board tun ? jedes in beschreibt eine verhaltensweise vom board

    "flip the first valid card but don't resolve yet" in {
      val b0 = Board(Vector(Card(0,"A"), Card(1,"A")))
      val (b1, res) = b0.choose(0) // eine karte aufdecken
      res shouldBe None // nach einem match wird nicht geschaut
      b1.cards(0).isFaceUp shouldBe true
      b1.selection shouldBe Some(0) // aufgedeckte karte wird gemerkt
    }

    "mark both as matched when the second card matches" in {
      val b0 = Board(Vector(Card(0,"A"), Card(1,"A")))
      val (b1, _) = b0.choose(0) // erste karte aufdecken
      val (b2, r2) = b1.choose(1) // zweite karte aufdecken
      r2 shouldBe Some(true) // wenn beide karten gleich sind --> return match
      b2.cards.forall(_.isMatched) shouldBe true // bleiben weiterhin aufgedeckt
      b2.selection shouldBe None // resetten für die nächste runde
    }

    "flip both face down when the second card doesn't match" in {
      val b0 = Board(Vector(Card(0,"A"), Card(1,"B")))
      val (b1, _) = b0.choose(0) // erste karte aufdecken
      val (b2, r2) = b1.choose(1) // zweite karte aufdecken
      r2 shouldBe Some(false) // zweite karte ist kein match
      b2.cards(0).isFaceUp shouldBe false
      b2.cards(1).isFaceUp shouldBe false // beide karten werden wieder umgedreht
      b2.selection shouldBe None // es gibt keine auswahl --> nächste runde
    }

    "ignore invalid clicks, same-card double click etc..." in {
      val matched = Card(0,"A", isFaceUp=true, isMatched=true)
      val faceUp  = Card(1,"B", isFaceUp=true)
      val b0 = Board(Vector(matched, faceUp))
      b0.choose(-1)._2 shouldBe None // out of range
      b0.choose(99)._2 shouldBe None //out of range
      b0.choose(0)._2  shouldBe None // eine karte die schon ein match hat
      b0.choose(1)._2  shouldBe None // aufgedeckte karte

      val b1 = Board(Vector(Card(0,"A"), Card(1,"A")))
      val (b2, _) = b1.choose(0)
      b2.choose(0)._2 shouldBe None // selbe karte wird aufgedeckt (erneutes flippen)
    }

    "return completion when all cards are matched" in { // wenn alle karten richtig auf gedeckt wurden --> success & completion
      val done = Board(Vector(
        Card(0,"A", isFaceUp=true, isMatched=true),
        Card(1,"A", isFaceUp=true, isMatched=true)
      ))
      done.allMatched shouldBe true
    }
  }
}
