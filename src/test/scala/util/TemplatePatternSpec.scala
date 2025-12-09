package util

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import model.{Board, Card}

class AsciiRendererSpec extends AnyWordSpec with Matchers {

  "AsciiRenderer" should {

    "render all cards face-down as [ ]" in {
      val cards = Vector(
        Card(0,"A"),
        Card(1,"B"),
        Card(2,"C"),
        Card(3,"D")
      )
      val out = AsciiRenderer().render(Board(cards))
      out should include ("[ ] [ ]")
    }

    "render face-up symbols correctly" in {
      val cards = Vector(
        Card(0,"A", isFaceUp = true),
        Card(1,"B")
      )
      val out = AsciiRenderer().render(Board(cards))
      out should include ("[A]")
      out should include ("[ ]")
    }

    "render matched cards as [✅]" in {
      val cards = Vector(
        Card(0,"A", isMatched = true),
        Card(1,"B")
      )
      val out = AsciiRenderer().render(Board(cards))
      out should include ("[✅]")
    }

    "layout cards correctly row-by-row for 2x3 grid" in {
      val cards = Vector(
        Card(0,"A",true), Card(1,"B",true),
        Card(2,"C",true), Card(3,"D",true),
        Card(4,"E",true), Card(5,"F",true)
      )
      val out = AsciiRenderer().render(Board(cards))
      out shouldBe "[A] [B] [C]\n[D] [E] [F]"
    }

    "should render placeholder cells when grid is larger than the number of cards" in {
      val cards = Vector(
        Card(0,"A"), Card(1,"B"), Card(2,"C"), Card(3,"D")
        // 4 Karten → sqrt(4)=2 → normalerweise 2×2 → KEIN else-Zweig!
      )

      // HACK: Wir faken einen "Level", indem wir computeGrid überschreiben:
      val renderer = new AsciiRenderer {
        override protected def computeGrid(board: Board) = (3, 3) // zwingt 3×3 Grid
      }

      val board = Board(cards)
      val out = renderer.render(board)

      out should include ("   ")   // placeholder Zelle, ELSE wurde ausgeführt
    }

  }
}
