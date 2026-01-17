package model.fileIoComponent.fileIoXmlImpl

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import model.{Board, Card}

final class FileIOXmlSpec extends AnyWordSpec with Matchers {

  "FileIOXml" should {

    "save and load a board" in {
      val fileIO = FileIO()
      val board  = Board(Vector(
        Card(0, "A"),
        Card(1, "A")
      ))

      fileIO.save(board)
      val loaded = fileIO.load 

      loaded.cards.map(_.symbol)
        .shouldBe(board.cards.map(_.symbol))

      loaded.cards.map(_.id)
        .shouldBe(board.cards.map(_.id))
    }
  }
}
