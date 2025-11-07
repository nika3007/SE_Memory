import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import java.io.{ByteArrayInputStream, ByteArrayOutputStream, PrintStream}

final class MemoryTuiSpec extends AnyWordSpec with Matchers {

  "MemoryTui" should {

    "exit gracefully on empty input (simulating ^D/^Z)" in {
      val input = new ByteArrayInputStream("\n".getBytes()) // leere Eingabe
      val output = new ByteArrayOutputStream()

      Console.withIn(input) {
        Console.withOut(output) {
          val tui = new MemoryTui(2, 2) // kleines Spiel für schnellen Test
          tui.run()
        }
      }

      val result = output.toString
      result should include ("Spiel beendet durch Eingabeabbruch")
    }

    "handle valid input and show board" in {
      val input = new ByteArrayInputStream("0\n1\n".getBytes())
      val output = new ByteArrayOutputStream()

      Console.withIn(input) {
        Console.withOut(output) {
          val tui = new MemoryTui(2, 2)
          tui.run()
        }
      }

      val result = output.toString
      result should include ("Memory gestartet")
      result should include ("Wähle eine Karte")
      result should include ("✅") // Treffer oder Spielende
    }

    "reject invalid input and show error" in {
      val input = new ByteArrayInputStream("abc\n0\n".getBytes())
      val output = new ByteArrayOutputStream()

      Console.withIn(input) {
        Console.withOut(output) {
          val tui = new MemoryTui(2, 2)
          tui.run()
        }
      }

      val result = output.toString
      result should include ("Ungültige Eingabe")
    }
  }
}

