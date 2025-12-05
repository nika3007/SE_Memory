import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

class MemoryMainSpec extends AnyWordSpec with Matchers {

  "The Memory main" should {

    "run without throwing an exception on automated input" in {

      // ❶ Fake Input generieren:
      val fakeInput =
        "fruits\n" +          // Theme-Auswahl
        ("0\n" * 50) +        // genug Eingaben, um Level 1 zu beenden
        "\n"                  // notfalls Abbruch

      val in  = new ByteArrayInputStream(fakeInput.getBytes())
      val out = new ByteArrayOutputStream()

      // ❷ Test starten
      Console.withIn(in) {
        Console.withOut(out) {
          noException should be thrownBy {
            runMemory()
          }
        }
      }

      // ❸ Ausgabe prüfen
      val text = out.toString
      text should include ("Memory gestartet")
      text should include ("Level 1")
    }
  }
}
