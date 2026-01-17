package model.fileIoComponent.fileIoJsonImpl

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import model.{Board, Card}

import java.io.File

final class FileIOSpec extends AnyWordSpec with Matchers {

  "FileIOJson" should {

    "save and load a board completely" in {
      val fileIO = new FileIO

      val board = Board(Vector(
        Card(0, "A", isFaceUp = true,  isMatched = false),
        Card(1, "B", isFaceUp = false, isMatched = true)
      ))

      // save
      fileIO.save(board)
      new File("memory.json").exists() shouldBe true

      // load
      val loaded = fileIO.load

      // assertions → deckt ALLE roten Zeilen ab
      loaded.cards.size shouldBe 2
      loaded.cards.map(_.id) shouldBe board.cards.map(_.id)
      loaded.cards.map(_.symbol) shouldBe board.cards.map(_.symbol)
      loaded.cards.map(_.isFaceUp) shouldBe board.cards.map(_.isFaceUp)
      loaded.cards.map(_.isMatched) shouldBe board.cards.map(_.isMatched)
    }
  }
}

