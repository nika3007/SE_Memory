package model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import model.Card

final class CardSpec extends AnyWordSpec with Matchers {

  "A Card" when {
    "created face down and unmatched" should {
      val card = Card(0, "🍎")

      "have correct id and symbol" in {
        card.id shouldBe 0
        card.symbol shouldBe "🍎"
      }

      "be face down" in {
        card.isFaceUp shouldBe false
      }

      "not be matched" in {
        card.isMatched shouldBe false
      }
    }

    "flipped" should {
      val card = Card(1, "🍇").flip

      "be face up" in {
        card.isFaceUp shouldBe true
      }

      "still not be matched" in {
        card.isMatched shouldBe false
      }
    }

    "marked as matched" should {
      val card = Card(2, "🍒").markMatched

      "be matched" in {
        card.isMatched shouldBe true
      }

      "retain symbol and id" in {
        card.symbol shouldBe "🍒"
        card.id shouldBe 2
      }
    }
  }
}
