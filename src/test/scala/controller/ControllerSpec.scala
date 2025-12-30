package controller.controllerComponent

import model.{Board, Card, MemoryGame, Level, BoardSizes, Difficulties, ThemeFactory, RandomAI, NoAI}
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import scala.collection.mutable
import controller.controllerComponent.ControllerAPI 

import controller.controllerComponent.controllerBaseImpl.ControllerImpl

import controller.controllerComponent.GameStatus

final class ControllerSpec extends AnyWordSpec with Matchers {

  // Hilfsfunktion, kleines board, um randomness zu vermeiden
  private def smallBoard(): Board =
    Board(Vector(
      Card(0, "A"), // pair A
      Card(1, "A"),
      Card(2, "B"), // pair B
      Card(3, "B")
    ))

  private def freshControllerWithBoard(): ControllerImpl =
    val theme = ThemeFactory.getTheme("fruits")
    val ai = RandomAI()
    val level = Level(BoardSizes.Medium4x4, Difficulties.Easy)
    val game = MemoryGame(theme, ai, Vector(level))
    val c = ControllerImpl(game)
    c.game.board = smallBoard()
    c

  // NEU: Hilfsfunktion um currentPlayer zu setzen (reflection)
  private def setCurrentPlayer(controller: ControllerImpl, player: String): Unit = {
    val field = controller.getClass.getDeclaredField("currentPlayer")
    field.setAccessible(true)
    field.set(controller, player)
  }

  "A Controller" should {

    "stop the game when input is null or empty" in {
      val c = freshControllerWithBoard()
      val before = c.board

      c.processInput(null) shouldBe false
      c.board shouldBe before

      c.processInput("   ") shouldBe false
      c.board shouldBe before
    }

    "reject non-numeric input and keep the board unchanged" in {
      val c = freshControllerWithBoard()
      val before = c.board

      val result = c.processInput("abc")
      result shouldBe true
      c.board shouldBe before
    }

    "reject out-of-range indices and keep the board unchanged" in {
      val c = freshControllerWithBoard()
      val before = c.board

      c.processInput("-1") shouldBe true
      c.processInput("99") shouldBe true
      c.board shouldBe before
    }

    "flip a valid first card and not resolve yet" in {
      val c = freshControllerWithBoard()
      val before = c.board

      val ok = c.processInput("0")
      ok shouldBe true

      val after = c.board
      after.cards(0).isFaceUp shouldBe true
      after.cards(1).isFaceUp shouldBe false
      after.cards(2).isFaceUp shouldBe false
      after.cards(3).isFaceUp shouldBe false
      after should not be theSameInstanceAs(before)
    }

    "mark a pair as matched when two matching cards are chosen" in {
      val c = freshControllerWithBoard()

      c.processInput("0") shouldBe true
      c.processInput("1") shouldBe true

      val b2 = c.board
      b2.cards(0).isMatched shouldBe true
      b2.cards(1).isMatched shouldBe true
    }

    "flip back non-matching cards after a failed second choice" in {
      val c = freshControllerWithBoard()

      c.processInput("0") shouldBe true
      c.processInput("2") shouldBe true

      Thread.sleep(1600) // Wartezeit für das automatische Zurückdrehen
      
      val b3 = c.board
      b3.cards(0).isFaceUp shouldBe false
      b3.cards(2).isFaceUp shouldBe false
    }

    "should set InvalidSelection when selecting the same face-up card again" in {
      val c = freshControllerWithBoard()

      c.processInput("0") shouldBe true
      c.gameStatus shouldBe GameStatus.FirstCard

      c.processInput("0") shouldBe true
      c.gameStatus shouldBe GameStatus.InvalidSelection(0)
    }

    "undo and restore the previous board after one move" in {
      val c = freshControllerWithBoard()
      val original = c.game.save()

      c.processInput("0")
      c.undo()

      c.board shouldBe original.board
    }

    "processInput(\"u\") should trigger undo" in {
      val c = freshControllerWithBoard()
      val original = c.game.save()

      c.processInput("0")
      c.processInput("u")

      c.board shouldBe original.board
    }

    "undo on empty history should keep the board unchanged" in {
      val c = freshControllerWithBoard()
      val before = c.board

      c.undo()
      c.board shouldBe before
    }

    "report aiEnabled correctly" in {
      val c1 = freshControllerWithBoard()
      c1.aiEnabled shouldBe true

      val theme = ThemeFactory.getTheme("fruits")
      val ai = NoAI()
      val level = Level(BoardSizes.Medium4x4, Difficulties.Easy)
      val game = MemoryGame(theme, ai, Vector(level))
      val c2 = ControllerImpl(game)
      c2.aiEnabled shouldBe false
    }

    "ignore human input when currentPlayer is ai" in {
      val c = freshControllerWithBoard()
      setCurrentPlayer(c, "ai") // Reflection verwenden
      val before = c.board
      c.processInput("0") shouldBe true
      c.board shouldBe before // keine Änderung
    }

    "do nothing when aiTurnFirst/Second called but currentPlayer is human" in {
      val c = freshControllerWithBoard()
      setCurrentPlayer(c, "human") // Reflection verwenden
      val before = c.board
      c.aiTurnFirst()
      c.aiTurnSecond()
      c.board shouldBe before
    }

    "execute aiTurnFirst and aiTurnSecond when currentPlayer is ai" in {
      val c = freshControllerWithBoard()
      setCurrentPlayer(c, "ai") // Reflection verwenden
      
      // beide Methoden sollten eine Karte aufdecken
      c.aiTurnFirst()
      c.board.cards.exists(_.isFaceUp) shouldBe true
      
      // Reset für zweiten Test
      setCurrentPlayer(c, "ai")
      c.game.board = smallBoard() // Board zurücksetzen
      
      c.aiTurnSecond()
      c.board.cards.exists(_.isFaceUp) shouldBe true
    }

    "print message when undo called on empty history" in {
      val c = freshControllerWithBoard()
      noException shouldBe thrownBy {
        c.undo()
      }
    }
    // Füge diese Tests am Ende deiner bestehenden Spec hinzu:

    "handle NoAI case in player switching" in {
      // Controller mit NoAI erstellen
      val theme = ThemeFactory.getTheme("fruits")
      val ai = NoAI()
      val level = Level(BoardSizes.Medium4x4, Difficulties.Easy)
      val game = MemoryGame(theme, ai, Vector(level))
      val c = ControllerImpl(game)
      c.game.board = smallBoard()
      
      val field = c.getClass.getDeclaredField("currentPlayer")
      field.setAccessible(true)
      
      // Start mit human
      field.set(c, "human")
      
      // Simuliere NoMatch durch direkten Aufruf von handleCardSelection
      val method = c.getClass.getDeclaredMethod("handleCardSelection", classOf[Int])
      method.setAccessible(true)
      
      // Erste Karte
      method.invoke(c, 0.asInstanceOf[AnyRef])
      // Zweite Karte (unterschiedlich - NoMatch)
      method.invoke(c, 2.asInstanceOf[AnyRef])
      
      Thread.sleep(1600)
      field.get(c) shouldBe "human" // Bleibt human bei NoAI
    }

    "switch player from human to ai after no match with active AI" in {
      val c = freshControllerWithBoard()
      val field = c.getClass.getDeclaredField("currentPlayer")
      field.setAccessible(true)
      
      field.set(c, "human")
      
      val method = c.getClass.getDeclaredMethod("handleCardSelection", classOf[Int])
      method.setAccessible(true)
      
      method.invoke(c, 0.asInstanceOf[AnyRef]) // Erste Karte
      method.invoke(c, 2.asInstanceOf[AnyRef]) // Zweite Karte (NoMatch)
      
      Thread.sleep(1600)
      field.get(c) shouldBe "ai" // Wechselt zu ai
    }

    "switch player from ai to human after no match with active AI" in {
      val c = freshControllerWithBoard()
      val field = c.getClass.getDeclaredField("currentPlayer")
      field.setAccessible(true)
      
      field.set(c, "ai")
      
      val method = c.getClass.getDeclaredMethod("handleCardSelection", classOf[Int])
      method.setAccessible(true)
      
      method.invoke(c, 0.asInstanceOf[AnyRef]) // Erste Karte
      method.invoke(c, 2.asInstanceOf[AnyRef]) // Zweite Karte (NoMatch)
      
      Thread.sleep(1600)
      field.get(c) shouldBe "human" // Wechselt zurück zu human
    }

    "verify resetBoard and NextRound status after no match" in {
      val c = freshControllerWithBoard()
      
      val method = c.getClass.getDeclaredMethod("handleCardSelection", classOf[Int])
      method.setAccessible(true)
      
      method.invoke(c, 0.asInstanceOf[AnyRef])
      method.invoke(c, 2.asInstanceOf[AnyRef]) // NoMatch
      
      Thread.sleep(1600)
      
      // Check: Alle nicht-gematched Karten sind face down
      c.board.cards.filterNot(_.isMatched).foreach(_.isFaceUp shouldBe false)
      // Check: NextRound Status
      c.gameStatus shouldBe GameStatus.NextRound
    }
  }
}