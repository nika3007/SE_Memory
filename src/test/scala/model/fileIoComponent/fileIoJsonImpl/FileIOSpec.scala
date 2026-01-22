package model.fileIoComponent.fileIoJsonImpl

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import model.{Board, Card}

import java.io.File

final class FileIOSpec extends AnyWordSpec with Matchers:

  "FileIOJson" should {

    "save and load board and touch every json field explicitly" in {
      val fileIO = new FileIO

      val board = Board(Vector(
        Card(0, "A", isFaceUp = true,  isMatched = false),
        Card(1, "B", isFaceUp = false, isMatched = true)
      ))

      fileIO.save(board)
      val file = new File("memory.json")
      file.exists() shouldBe true

      val loaded = fileIO.load

      loaded.cards.size shouldBe 2

      val c0 = loaded.cards.head
      val c1 = loaded.cards(1)

      c0.id shouldBe 0
      c0.symbol shouldBe "A"
      c0.isFaceUp shouldBe true
      c0.isMatched shouldBe false

      c1.id shouldBe 1
      c1.symbol shouldBe "B"
      c1.isFaceUp shouldBe false
      c1.isMatched shouldBe true

      file.delete() shouldBe true
    }
  }