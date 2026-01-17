package model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import scala.collection.mutable

final class AIStrategySpec extends AnyWordSpec with Matchers {

  // -------------------------------------------------------
  // Hilfsfunktionen
  // -------------------------------------------------------
  private def board(symbols: Vector[String]): Board =
    Board(symbols.zipWithIndex.map { case (s, i) => Card(i, s) })

  private def memoryOf(ai: AnyRef): mutable.Map[String, mutable.ListBuffer[Int]] =
    val f = ai.getClass.getDeclaredFields.find(_.getName.contains("memory")).get
    f.setAccessible(true)
    f.get(ai).asInstanceOf[mutable.Map[String, mutable.ListBuffer[Int]]]

  private def setFirstPick(ai: AnyRef, value: Option[(String, Int)]): Unit =
    val f = ai.getClass.getDeclaredField("firstPick")
    f.setAccessible(true)
    f.set(ai, value)

  // =======================================================
  // NoAI
  // =======================================================
  "NoAI" should {
    "always return -1" in {
      val ai = NoAI()
      ai.chooseCard(board(Vector("A", "A"))) shouldBe -1
    }
  }

  // =======================================================
  // RandomAI
  // =======================================================
  "RandomAI" should {

    "choose only valid indices" in {
      val ai = RandomAI()
      val b = board(Vector("A", "A", "B", "B"))

      for (_ <- 1 to 50) {
        val i = ai.chooseCard(b)
        i should (be >= 0 and be < b.cards.size)
      }
    }

    "never choose matched cards" in {
      val ai = RandomAI()
      val b = Board(Vector(
        Card(0, "A", isMatched = true),
        Card(1, "A"),
        Card(2, "B"),
        Card(3, "B")
      ))

      ai.chooseCard(b) should not be 0
    }
  }

  // =======================================================
  // MediumAI
  // =======================================================
  "MediumAI" should {

    "pick random card when memory is empty" in {
      val ai = MediumAI()
      val i = ai.chooseCard(board(Vector("A", "A", "B", "C")))
      i should (be >= 0 and be < 4)
    }

    "pick known pair if memory contains two positions" in {
      val ai = MediumAI()
      val mem = memoryOf(ai)
      mem += "A" -> mutable.ListBuffer(0, 1)

      val i = ai.chooseCard(board(Vector("A", "A", "B", "C")))
      Set(0, 1) should contain (i)
    }

    "pick single known card if only one is known" in {
      val ai = MediumAI()
      val mem = memoryOf(ai)
      mem += "A" -> mutable.ListBuffer(0)

      ai.chooseCard(board(Vector("A", "A", "B", "C"))) shouldBe 0
    }

    "cleanup removes face-up and matched cards" in {
      val ai = MediumAI()
      val mem = memoryOf(ai)
      mem += "A" -> mutable.ListBuffer(0, 1)
      mem += "B" -> mutable.ListBuffer(2)

      val b = Board(Vector(
        Card(0, "A").flip,
        Card(1, "A").flip,
        Card(2, "B", isMatched = true),
        Card(3, "C")
      ))

      ai.chooseCard(b)

      mem("A").isEmpty shouldBe true
      mem("B").isEmpty shouldBe true
    }

    "evict oldest memory entry when maxEntries exceeded" in {
      val ai = MediumAI()
      val mem = memoryOf(ai)

      mem += "A" -> mutable.ListBuffer(0)
      mem += "B" -> mutable.ListBuffer(1)
      mem += "C" -> mutable.ListBuffer(2)
      mem += "D" -> mutable.ListBuffer(3)

      ai.chooseCard(board(Vector("E","E","F","F","G","G","H","H")))

      mem.size should be <= 4
    }
  }

  // =======================================================
  // HardAI
  // =======================================================
  "HardAI" should {

    "pick random card if memory empty" in {
      val ai = HardAI()
      val i = ai.chooseCard(board(Vector("A","A","B","C")))
      i should (be >= 0 and be < 4)
    }

    "pick known pair if available" in {
      val ai = HardAI()
      val mem = memoryOf(ai)
      mem += "A" -> mutable.ListBuffer(0,1)

      val i = ai.chooseCard(board(Vector("A","A","B","C")))
      Set(0,1) should contain (i)
    }

    "cleanup removes matched cards" in {
      val ai = HardAI()
      val mem = memoryOf(ai)
      mem += "A" -> mutable.ListBuffer(0)

      val b = Board(Vector(
        Card(0,"A", isMatched=true),
        Card(1,"B")
      ))

      ai.chooseCard(b)
      mem("A").isEmpty shouldBe true
    }
  }

  // =======================================================
  // MemoryAI (Pro)
  // =======================================================
  "MemoryAI" should {

    "pick random card on first move" in {
      val ai = MemoryAI()
      val i = ai.chooseCard(board(Vector("A","A","B","C")))
      i should (be >= 0 and be < 4)
    }

    "use firstPick to select matching second card" in {
      val ai = MemoryAI()
      val mem = memoryOf(ai)
      mem += "A" -> mutable.ListBuffer(0,1)
      setFirstPick(ai, Some("A" -> 0))

      ai.chooseCard(board(Vector("A","A","B","C"))) shouldBe 1
    }

    "pick known pair when firstPick is empty" in {
      val ai = MemoryAI()
      val mem = memoryOf(ai)
      mem += "A" -> mutable.ListBuffer(0,1)

      val i = ai.chooseCard(board(Vector("A","A","B","C")))
      Set(0,1) should contain (i)
    }

    "fallback to random if no known match exists" in {
      val ai = MemoryAI()
      val mem = memoryOf(ai)
      mem += "A" -> mutable.ListBuffer(0)
      setFirstPick(ai, Some("A" -> 0))

      val b = Board(Vector(
        Card(0,"A").flip,
        Card(1,"A").flip,
        Card(2,"B"),
        Card(3,"C")
      ))

      val i = ai.chooseCard(b)
      i should (be >= 0 and be < 4)
    }

    "cleanup removes matched cards from memory" in {
      val ai = MemoryAI()
      val mem = memoryOf(ai)
      mem += "A" -> mutable.ListBuffer(0)

      val b = Board(Vector(
        Card(0,"A", isMatched=true),
        Card(1,"B")
      ))

      ai.chooseCard(b)
      mem("A").isEmpty shouldBe true
    }
  }
}
