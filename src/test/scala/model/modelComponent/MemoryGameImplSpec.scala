package model.modelComponent

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

import model.*
import model.modelComponent.implModel.MemoryGameImpl

final class MemoryGameImplCoverageSpec extends AnyWordSpec with Matchers:

  private def mkLevels(): Vector[Level] =
    Vector(
      Level(BoardSizes.Small2x2, Difficulties.Easy),
      Level(BoardSizes.Medium4x4, Difficulties.Easy)
    )

  "MemoryGameImpl" should {

    "cover all getters including levelsCount" in {
      val levels = mkLevels()
      val g = new MemoryGameImpl(ThemeFactory.getTheme("emoji"), NoAI(), levels)

      val themeVal = g.theme
      val aiVal = g.ai
      val countVal = g.levelsCount
      val idxVal = g.currentLevelIndex
      val numVal = g.currentLevelNumber
      val lvlVal = g.currentLevel
      val boardSize = g.board.cards.size

      themeVal shouldBe ThemeFactory.getTheme("emoji")
      aiVal shouldBe a [NoAI]
      countVal shouldBe levels.size
      idxVal shouldBe 0
      numVal shouldBe 1
      lvlVal shouldBe levels.head
      boardSize shouldBe levels.head.size.rows * levels.head.size.cols
    }

    "cover save and restore" in {
      val g = new MemoryGameImpl(ThemeFactory.getTheme("emoji"), NoAI(), mkLevels())

      val before = g.board
      val m = g.save()

      g.board = g.board.copy(cards = g.board.cards.map(_.flip))
      g.board should not be before

      g.restore(m)
      g.board shouldBe before
    }

    "cover nextLevel true and false branches" in {
      val g = new MemoryGameImpl(ThemeFactory.getTheme("emoji"), NoAI(), mkLevels())

      val first = g.nextLevel()
      first shouldBe true
      g.currentLevelIndex shouldBe 1

      val second = g.nextLevel()
      second shouldBe false
      g.currentLevelIndex shouldBe 1
    }

    "cover setTheme and board rebuild" in {
      val g = new MemoryGameImpl(ThemeFactory.getTheme("emoji"), NoAI(), mkLevels())

      val before = g.board.cards.map(_.symbol).toSet
      g.setTheme("animals")
      val after = g.board.cards.map(_.symbol).toSet

      g.theme shouldBe ThemeFactory.getTheme("animals")
      after should not be before
    }

    "cover all setAI branches including default" in {
      val g = new MemoryGameImpl(ThemeFactory.getTheme("emoji"), NoAI(), mkLevels())

      g.setAI("none")
      g.ai shouldBe a [NoAI]

      g.setAI("easy")
      g.ai shouldBe a [RandomAI]

      g.setAI("medium")
      g.ai shouldBe a [MediumAI]

      g.setAI("hard")
      g.ai shouldBe a [HardAI]

      g.setAI("pro")
      g.ai shouldBe a [MemoryAI]

      g.setAI("???")
      g.ai shouldBe a [RandomAI]
    }
  }