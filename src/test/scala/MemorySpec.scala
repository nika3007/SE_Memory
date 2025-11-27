import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

class MemoryMainSpec extends AnyWordSpec with Matchers {

  "The Memory main" should {
    "run without throwing an exception on a single empty input line" in {
      // bei enter cancel game
      val in  = new ByteArrayInputStream("\n".getBytes())

      // 2) Ausgabe abfangen (optional)
      val out = new ByteArrayOutputStream()

      Console.withIn(in) {
        Console.withOut(out) {
          noException should be thrownBy {
            runMemory()
          }
        }
      }

      // prüfe ob ausgabe entsprechend den vorgaben ist
      val text = out.toString
      text should include ("Memory gestartet")
    }
  }
}
