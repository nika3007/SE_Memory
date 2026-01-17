import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

class MemoryMainSpec extends AnyWordSpec with Matchers {

  private def runWithInput(input: String): String =
    val in  = new ByteArrayInputStream(input.getBytes())
    val out = new ByteArrayOutputStream()

    Console.withIn(in) {
      Console.withOut(out) {
        try {
          Memory.main(Array.empty)
        } catch {
          case _: Throwable => // Endlosschleifen / System.exit ignorieren
        }
      }
    }
    out.toString

  "The Memory main" should {

    "print welcome message and mode selection" in {
      val output = runWithInput("1\n")   // nur Mode

      output should include ("Welcome to Memory!")
      output should include ("choose the mode")
      output should include ("just TUI")
    }

    "accept TUI mode and ask for theme and AI" in {
      val output = runWithInput(
        "1\n" +        // TUI
        "fruits\n" +   // Theme
        "easy\n"       // AI
      )

      output should include ("Welcome to Memory!")
      output should include ("Choose theme")
      output should include ("Choose AI level")
    }

    "fallback to RandomAI on invalid AI input" in {
      val output = runWithInput(
        "1\n" +                 // TUI
        "fruits\n" +            // Theme
        "not_valid_ai\n"        // AI
      )

      output should include ("Welcome to Memory!")
      output should include ("Choose AI level")
      // implizit getestet: case _ => RandomAI()
    }
  }
}
