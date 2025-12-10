package controller

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

final class GameStatusSpec extends AnyWordSpec with Matchers {

  "GameStatus.message" should {

    "return message for InvalidSelection with correct index" in {
      GameStatus.message(GameStatus.InvalidSelection(5)) shouldBe
        "❗ Karte 5 kann nicht gewählt werden."
    }

    "return empty message for SecondCard" in {
      GameStatus.message(GameStatus.SecondCard) shouldBe ""
    }

    "return message for Match" in {
      GameStatus.message(GameStatus.Match) shouldBe
        "🎯 Match! nochmal dran!"
    }

    "return message for NoMatch" in {
      GameStatus.message(GameStatus.NoMatch) shouldBe
        "❌ No Match!"
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
