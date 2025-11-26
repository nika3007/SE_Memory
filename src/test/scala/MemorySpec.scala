import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers


class MemoryMainSpec extends AnyWordSpec with Matchers {

  "The Memory main" should {

    "run without throwing an exception" in {
      noException shouldBe thrownBy {
        runMemory()   // @main Funktion
      }
    }
  }
}


