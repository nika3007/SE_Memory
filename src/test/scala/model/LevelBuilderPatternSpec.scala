package model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class LevelBuilderSpec extends AnyWordSpec with Matchers {

  "A LevelBuilder" should {

    "build a default level" in {
      val lvl = LevelBuilder().build()

      lvl.size shouldBe BoardSizes.Medium4x4
      lvl.difficulty shouldBe Difficulties.Easy
      lvl.timeLimitSeconds shouldBe 0
    }

    "build a custom level" in {
      val lvl =
        LevelBuilder()
          .setSize(BoardSizes.Large6x6)
          .setDifficulty(Difficulties.Hard)
          .setTimeLimit(120)
          .build()

      lvl.size shouldBe BoardSizes.Large6x6
      lvl.difficulty.matchAmount shouldBe 4
      lvl.timeLimitSeconds shouldBe 120
    }
  }
}
