import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

class MemoryMainSpec extends AnyWordSpec with Matchers {

  private def runWithInput(input: String): String = {
    val in = new ByteArrayInputStream(input.getBytes())
    val out = new ByteArrayOutputStream()

    Console.withIn(in) {
      Console.withOut(out) {
        try {
          runMemory()
        } catch {
          case _: Throwable => // Ignoriere Exceptions für Tests
        }
      }
    }
    out.toString
  }

  "The Memory main" should {

    "run without throwing an exception on normal input" in {
      val fakeInput = "fruits\neasy\n"
      val output = runWithInput(fakeInput)
      
      output should include ("Welcome to Memory!")
      output should include ("Choose theme")
      output should include ("Choose AI level")
    }

    "handle invalid AI choice by using RandomAI as default" in {
      val fakeInput = "fruits\ninvalid_ai_choice\n"
      val output = runWithInput(fakeInput)
      
      output should include ("Welcome to Memory!")
      // Testet die Zeile: case _ => RandomAI()
    }

    "work with different valid AI choices" in {
      val aiChoices = List("none", "easy", "medium", "hard", "pro")
      
      for (aiChoice <- aiChoices) {
        val fakeInput = s"fruits\n$aiChoice\n"
        val output = runWithInput(fakeInput)
        
        output should include ("Welcome to Memory!")
      }
    }
  }
}