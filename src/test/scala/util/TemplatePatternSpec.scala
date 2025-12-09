package util

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import model.{Board, Card}

class AsciiRendererSpec extends AnyWordSpec with Matchers {

  "AsciiRenderer" should {

    "render all cards face-down as [ ]" in {
      val cards = Vector(
        Card(0, "A"),
        Card(1, "B"),
        Card(2, "C"),
        Card(3, "D")
      )

      val board = Board(cards)
      val renderer = AsciiRenderer()

      val out = renderer.render(board)

      out should include ("[ ] [ ]")
    }

    "render face-up symbols correctly" in {
      val cards = Vector(
        Card(0, "A", isFaceUp = true),
        Card(1, "B"),
        Card(2, "C"),
        Card(3, "D")
      )

      val board = Board(cards)
      val renderer = AsciiRenderer()

      val out = renderer.render(board)

      out should include ("[A]")         // face-up
      out should include ("[ ]")          // face-down still present
    }

    "render matched cards as [✅]" in {
      val cards = Vector(
        Card(0, "A", isMatched = true),
        Card(1, "B"),
        Card(2, "C"),
        Card(3, "D")
      )

      val board = Board(cards)
      val renderer = AsciiRenderer()

      val out = renderer.render(board)

      out should include ("[✅]")
    }

    "layout cards correctly row by row for 2x3 grid" in {
      val cards = Vector(
        Card(0,"A",true), Card(1,"B",true),
        Card(2,"C",true), Card(3,"D",true),
        Card(4,"E",true), Card(5,"F",true)
      )

      val board = Board(cards)
      val renderer = AsciiRenderer()

      val out = renderer.render(board)

      // FIXED: richtige 2×3-Formatierung
      out shouldBe "[A] [B] [C]\n[D] [E] [F]"

      // kleine Zusatzzeile für vollen Branch in formatMatrix()
      out.split("\n").length shouldBe 2
    }

    "compute grid fallback for prime-like sizes" in {
      val cards = Vector(
        Card(0,"A"), Card(1,"B"), Card(2,"C"),
        Card(3,"D"), Card(4,"E"), Card(5,"F"),
        Card(6,"G")
      )

      val board = Board(cards)
      val renderer = AsciiRenderer()

      val out = renderer.render(board)

      // computeGrid → rows = 1, cols = 7
      out.split("\n").length shouldBe 1
      out should include ("[ ] [ ] [ ]")
    }

  }
}
