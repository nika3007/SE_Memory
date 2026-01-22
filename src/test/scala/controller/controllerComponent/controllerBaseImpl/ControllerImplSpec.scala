package controller.controllerComponent.controllerBaseImpl

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

import controller.controllerComponent.GameStatus
import model.*
import model.modelComponent.implModel.MemoryGameImpl

final class ControllerImplSpec extends AnyWordSpec with Matchers:

  private def mkGame(levelCount: Int, ai: AIPlayer): MemoryGameImpl =
    val theme = ThemeFactory.getTheme("fruits")
    val levels = Vector.fill(levelCount)(
      Level(BoardSizes.Small2x2, Difficulties.Easy)
    )
    new MemoryGameImpl(theme, ai, levels)

  private def mkBoard(): Board =
    Board(
      Vector(
        Card(0, "A"), Card(1, "A"),
        Card(2, "B"), Card(3, "B")
      )
    )

  "ControllerImpl" should {

    "cover processInput null / empty" in {
      val game = mkGame(1, NoAI())
      game.board = mkBoard()
      val c = new ControllerImpl(game)

      c.processInput(null) shouldBe false
      c.processInput("") shouldBe false
      c.processInput("   ") shouldBe false
    }

    "cover InvalidSelection(-1) branch" in {
      val game = mkGame(1, NoAI())
      game.board = mkBoard()
      val c = new ControllerImpl(game)

      c.processInput("abc") shouldBe true
      c.gameStatus shouldBe GameStatus.InvalidSelection(-1)
    }

    "cover execute, undo, redo with empty and non-empty stacks" in {
      val game = mkGame(1, NoAI())
      game.board = mkBoard()
      val c = new ControllerImpl(game)

      noException shouldBe thrownBy(c.undo())
      noException shouldBe thrownBy(c.redo())

      c.processInput("0")
      c.undo()
      c.redo()
    }

    "cover early return when currentPlayer == ai" in {
      val game = mkGame(1, RandomAI())
      game.board = mkBoard()
      val c = new ControllerImpl(game)

      c._currentPlayer = "ai"
      val before = c.board
      c.processInput("0") shouldBe true
      c.board shouldBe before
    }

    "cover aiEnabled and guarded ai turns" in {
      val game = mkGame(1, RandomAI())
      game.board = mkBoard()
      val c = new ControllerImpl(game)

      c.aiEnabled shouldBe true

      c._currentPlayer = "human"
      c.aiTurnFirst()
      c.aiTurnSecond()

      c._currentPlayer = "ai"
      c.aiTurnFirst()
      c.aiTurnSecond()
    }

    "cover handleCardSelection invalid same-card branch" in {
      val game = mkGame(1, NoAI())
      game.board = mkBoard()
      val c = new ControllerImpl(game)

      c.handleCardSelection(0)
      c.handleCardSelection(0)

      c.gameStatus shouldBe GameStatus.InvalidSelection(0)
    }

    "cover mismatch path with cancelThread = false" in {
      val game = mkGame(1, RandomAI())
      game.board = mkBoard()
      val c = new ControllerImpl(game)

      c._currentPlayer = "human"
      c.cancelThread = false

      c.handleCardSelection(0)
      c.handleCardSelection(2)

      c.gameStatus shouldBe GameStatus.NoMatch

      Thread.sleep(1400)

      c.gameStatus shouldBe GameStatus.NextRound
      c.currentPlayer shouldBe "ai"

      c.board.cards.foreach { c =>
        c.isFaceUp shouldBe false
        c.isMatched shouldBe false
      }
    }

    "cover mismatch path with cancelThread = true" in {
      val game = mkGame(1, RandomAI())
      game.board = mkBoard()
      val c = new ControllerImpl(game)

      c._currentPlayer = "human"
      c.cancelThread = false

      c.handleCardSelection(0)
      c.handleCardSelection(2)

      c.cancelThread = true
      Thread.sleep(1400)

      c.gameStatus shouldBe GameStatus.NoMatch
      c.currentPlayer shouldBe "human"
    }

    "cover advanceLevelIfPossible hasNext == true" in {
      val game = mkGame(2, NoAI())
      game.board = mkBoard()
      val c = new ControllerImpl(game)

      c.handleCardSelection(0)
      c.handleCardSelection(1)
      c.handleCardSelection(2)
      c.handleCardSelection(3)

      c.gameStatus shouldBe GameStatus.NextRound
      game.currentLevelIndex shouldBe 1
    }

    "cover advanceLevelIfPossible hasNext == false" in {
      val game = mkGame(1, NoAI())
      game.board = mkBoard()
      val c = new ControllerImpl(game)

      c.handleCardSelection(0)
      c.handleCardSelection(1)
      c.handleCardSelection(2)
      c.handleCardSelection(3)

      c.gameStatus shouldBe GameStatus.LevelComplete
    }
  }