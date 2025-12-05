package util

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import model.{Board, Card}

class HintSystemSpec extends AnyWordSpec with Matchers {

  "The HintSystem" should {

    "find a pair when one exists" in {
      val cards = Vector(
        Card(0, "A"),
        Card(1, "B"),
        Card(2, "A"),   // Match mit 0
        Card(3, "C")
      )
      val board = Board(cards)

      val hint = HintSystem.getHint(board)

      // Muss existieren
      hint should not be None

      // Reihenfolge egal → selbst in Set umwandeln
      val (a, b) = hint.get
      Set(a, b) shouldBe Set(0, 2)
    }

    "ignore cards that are already matched" in {
      val cards = Vector(
        Card(0, "A", isMatched = true),
        Card(1, "A", isMatched = true),
        Card(2, "B", isMatched = true),
        Card(3, "B", isMatched = true)
      )
      val board = Board(cards)

      HintSystem.getHint(board) shouldBe None
    }
    
    "return None if no unmatched pair exists" in {
      val cards = Vector(
        Card(0, "A"),
        Card(1, "B"),
        Card(2, "C"),
        Card(3, "D")
      )
      val board = Board(cards)

      HintSystem.getHint(board) shouldBe None
    }

    "not use faceUp as a requirement (hints work anytime)" in {
      val cards = Vector(
        Card(0, "A", isFaceUp = true),
        Card(1, "A", isFaceUp = false)
      )
      val board = Board(cards)

      val hint = HintSystem.getHint(board)

      hint should not be None

      val (a, b) = hint.get
      Set(a, b) shouldBe Set(0, 1)
    }
  }
}
