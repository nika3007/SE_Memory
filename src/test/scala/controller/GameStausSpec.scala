package controller

package controller

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

final class GameStatusSpec extends AnyWordSpec with Matchers {

  "GameStatus.message" should {

    "return message for InvalidSelection with correct index" in {
      GameStatus.message(GameStatus.InvalidSelection(5)) shouldBe
        "❗ Karte 5 kann nicht gewählt werden."
    }

    "return message for SecondCard" in {
      GameStatus.message(GameStatus.SecondCard) shouldBe
        "zweite Karte wählen..."
    }

    "return message for Match" in {
      GameStatus.message(GameStatus.Match) shouldBe
        "✅ Treffer!"
    }

    "return message for NoMatch" in {
      GameStatus.message(GameStatus.NoMatch) shouldBe
        "❌ Kein Treffer!"
    }

    "return message for NextRound" in {
      GameStatus.message(GameStatus.NextRound) shouldBe
        "nächste Runde..."
    }

    "return empty string for Idle" in {
      GameStatus.message(GameStatus.Idle) shouldBe ""
    }
  }
}