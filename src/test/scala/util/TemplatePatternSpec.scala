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

      out should include ("[A]")
      out should not include "[ ] [A]" // A steht an Position 0
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

    "layout cards correctly row by row" in {
      // 2x2 Layout
      val cards = Vector(
        Card(0, "A"),
        Card(1, "B"),
        Card(2, "C"),
        Card(3, "D")
      )

      val board = Board(cards)
      val renderer = AsciiRenderer()

      val out = renderer.render(board)

      val rows = out.split("\n")
      rows.length shouldBe 2      // 2 rows
      rows(0).trim shouldBe "[ ] [ ]"
      rows(1).trim shouldBe "[ ] [ ]"
    }
  }
}
