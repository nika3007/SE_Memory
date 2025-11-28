package model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class MemoryGameSpec extends AnyWordSpec with Matchers {

  // Fake-LevelBuilder: deterministische Levels (keine Random-Fehler)
  val level1 = Level(BoardSize(2, 2), Difficulty(2))
  val level2 = Level(BoardSize(2, 2), Difficulty(2))

  val levels = Vector(level1, level2)

  // Einfaches Theme
  val theme = new FruitsTheme

  // Dummy-AI für Tests
  object DummyAI extends AIPlayer:
    def chooseCard(board: Board): Int = 0

  "A MemoryGame" should {

    "build a board matching the level size" in {
      val game = MemoryGame(theme, DummyAI, Vector(level1))
      val b = game.board

      b.cards.size shouldBe 4
    }

    "create pairs of symbols for the board" in {
      val game = MemoryGame(theme, DummyAI, Vector(level1))
      val b = game.board

      val symbols = b.cards.map(_.symbol)
      val grouped = symbols.groupBy(identity).map(_._2.size)

      grouped should contain only 2     // jedes Symbol kommt 2x vor
    }

    "save() should capture the current board state (Memento)" in {
      val game = MemoryGame(theme, DummyAI, Vector(level1))
      val before = game.board
      val m = game.save()

      m.board.cards shouldEqual before.cards
    }

    "restore() should revert to the saved board state (Memento)" in {
      val game = MemoryGame(theme, DummyAI, Vector(level1))
      val m = game.save()

      // Board ändern
      val changed = game.board.copy(
        cards = game.board.cards.updated(0, game.board.cards(0).flip)
      )
      game.board = changed

      // Restore
      game.restore(m)

      game.board.cards shouldEqual m.board.cards
    }

    "nextLevel should move to the next level and rebuild the board" in {
      val game = MemoryGame(theme, DummyAI, levels)
      val board1 = game.board

      val result = game.nextLevel()

      result shouldBe true
      game.currentLevel shouldBe level2
      game.board.cards.size shouldBe 4
      game.board.cards should not equal board1.cards
    }

    "nextLevel should return false if no more levels exist" in {
      val game = MemoryGame(theme, DummyAI, Vector(level1))

      game.nextLevel() shouldBe false
    }
  }
}
