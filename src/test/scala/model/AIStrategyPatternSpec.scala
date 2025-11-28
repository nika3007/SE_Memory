package model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class AIStrategySpec extends AnyWordSpec with Matchers {

  "RandomAI" should {
    "always choose a valid hidden card" in {
      val b = Board(Vector(
        Card(0,"A"), Card(1,"A"),
        Card(2,"B"), Card(3,"B")
      ))
      val ai = new RandomAI

      val choice = ai.chooseCard(b)
      choice shouldBe >= (0)
      choice shouldBe < (4)
    }
  }

  "MemoryAI" should {
    "remember previously seen symbols" in {
      val ai = new MemoryAI
      val b = Board(Vector(
        Card(0,"A", isFaceUp = false),
        Card(1,"A", isFaceUp = false),
        Card(2,"B"),
        Card(3,"B")
      ))

      val first = ai.chooseCard(b)
      val second = ai.chooseCard(b)

      first should not equal second
    }
  }
}
