package aview

import controller.Controller
import model.{Board, Card}

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import java.io.ByteArrayOutputStream
import java.io.ByteArrayInputStream


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

    "call update() to print the board" in {
      val cards = Vector(Card(0,"A"), Card(1,"A"), Card(2,"B"), Card(3,"B"))
      val controller = new Controller(2, 2)
      controller.game.board = Board(cards)
      val tui = new MemoryTui(controller)

      val out = new ByteArrayOutputStream()
      Console.withOut(out) {
        tui.update()  // ruft boardToString intern auf
      }

      val output = out.toString.trim
      output should include ("[ ] [ ]")
      output should include ("\n")
    }

    "run() should print start message and end immediately on empty input" in {
      val input = new ByteArrayInputStream("\n".getBytes())
      val output = new ByteArrayOutputStream()

      val controller = new Controller(2, 2)
      val tui = new MemoryTui(controller)

      Console.withIn(input) {
        Console.withOut(output) {
          tui.run()
        }
      }

      val text = output.toString
      text should include ("Memory gestartet")
      text should include ("Spiel beendet durch Eingabeabbruch")
    }

    }
  }


