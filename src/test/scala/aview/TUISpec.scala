package aview

import controller.Controller
import model.{Board, Card}

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
//import java.io.ByteArrayOutputStream
//import java.io.ByteArrayInputStream


class MemoryTuiSpec extends AnyWordSpec with Matchers {

  "A Memory Tui" should {

    "show all cards face down as [ ]" in { // alle karten werden verdeckt angezeit sobald ein neues board generiert wurde
      val cards = Vector( //für testzwecke vordefiniert in einem vektor
        Card(0, "A"), Card(1, "A"),
        Card(2, "B"), Card(3, "B")
      )
      val controller = new Controller(2, 2) // um randomness zu meiden im test
      controller.game.board = Board(cards)
      val tui = new MemoryTui(controller)

      tui.boardToString shouldBe "[ ] [ ]\n[ ] [ ]" // verdeckte karten sehen [ ] so aus
    }

    "show symbols for face-up cards" in { // symbole werden angezeigt sobald die karten umgedreht werden
      val cards = Vector(
        Card(0, "A", isFaceUp = true), // test aufgedeckte karten aber noch kein match
        Card(1, "A"),
        Card(2, "B", isFaceUp = true),
        Card(3, "B")
      )
      val controller = new Controller(2, 2)
      controller.game.board = Board(cards)
      val tui = new MemoryTui(controller)

      tui.boardToString shouldBe "[A] [ ]\n[B] [ ]" // aufgedeckte karten anzeigen
    }

    "show matched cards as [✅]" in {
      val cards = Vector(
        Card(0, "A", isFaceUp = true, isMatched = true), // match von zwei gleichen karten die auch aufgedeckt sind
        Card(1, "A", isFaceUp = true, isMatched = true),
        Card(2, "B"), Card(3, "B")
      )
      val controller = new Controller(2, 2)
      controller.game.board = Board(cards)
      val tui = new MemoryTui(controller)

      tui.boardToString shouldBe "[✅] [✅]\n[ ] [ ]" // match wird mit einem grünen hacken angezeigt
    }

    "layout cards row-by-row using r*cols+c" in { // tui muss das board mit den karten im richtigen format ausgeben bzw im richtigen layout
      val cards = Vector(
        Card(0,"A",true), Card(1,"B",true),
        Card(2,"C",true), Card(3,"D",true),
        Card(4,"E",true), Card(5,"F",true)
      )
      val controller = new Controller(3, 2)
      controller.game.board = Board(cards)
      val tui = new MemoryTui(controller)

      tui.boardToString shouldBe "[A] [B]\n[C] [D]\n[E] [F]" // karten werden nach der form angezeigt i = row * cols + col
    }

    
    // 🎉 Neue Tests im Stil des Profs:
    "flip a card on numeric input" in {
      val controller = new Controller(2,2)
      controller.game.board = Board(Vector(
        Card(0,"A"), Card(1,"A"),
        Card(2,"B"), Card(3,"B")
      ))
      val tui = new MemoryTui(controller)

      tui.processInputLine("0")

      controller.board.cards(0).isFaceUp shouldBe true
    }

    "ignore non-numeric input" in {
      val controller = new Controller(2,2)
      val before = controller.board
      val tui = new MemoryTui(controller)

      tui.processInputLine("abc")

      controller.board shouldBe before
    }

    "ignore out-of-range input" in {
      val controller = new Controller(2,2)
      val before = controller.board
      val tui = new MemoryTui(controller)

      tui.processInputLine("999")

      controller.board shouldBe before
    }

    "mark matched cards after two matching inputs" in {
      val controller = new Controller(2,2)
      controller.game.board = Board(Vector(
        Card(0,"A"), Card(1,"A"),
        Card(2,"B"), Card(3,"B")
      ))
      val tui = new MemoryTui(controller)

      tui.processInputLine("0")
      tui.processInputLine("1")

      controller.board.cards(0).isMatched shouldBe true
      controller.board.cards(1).isMatched shouldBe true
    }

    "flip back mismatched cards" in {
      val controller = new Controller(2,2)
      controller.game.board = Board(Vector(
        Card(0,"A"), Card(1,"A"),
        Card(2,"B"), Card(3,"C")
      ))
      val tui = new MemoryTui(controller)

      tui.processInputLine("0")
      tui.processInputLine("2")

      controller.board.cards(0).isFaceUp shouldBe false
      controller.board.cards(2).isFaceUp shouldBe false
    }
  }
}