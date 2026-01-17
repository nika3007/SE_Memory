package model.fileIoComponent.fileIoXmlImpl

import model.fileIoComponent.FileIOInterface
import model.{Board, Card}

import scala.xml.{NodeSeq, PrettyPrinter}

class FileIO extends FileIOInterface {

  override def load: Board = {
    val file = scala.xml.XML.loadFile("memory.xml")

    val cards: Vector[Card] =
      (file \\ "card").map { c =>
        Card(
          id        = (c \ "@id").text.toInt,
          symbol    = (c \ "@symbol").text,
          isFaceUp  = (c \ "@isFaceUp").text.toBoolean,
          isMatched = (c \ "@isMatched").text.toBoolean
        )
      }.toVector

    Board(cards)
  }

  override def save(board: Board): Unit = {
    import java.io._
    val pw = new PrintWriter(new File("memory.xml"))
    val pp = new PrettyPrinter(120, 4)
    pw.write(pp.format(boardToXml(board)))
    pw.close()
  }

  private def boardToXml(board: Board) =
    <board>
      {
        board.cards.map { c =>
          <card
            id={c.id.toString}
            symbol={c.symbol}
            isFaceUp={c.isFaceUp.toString}
            isMatched={c.isMatched.toString}/>
        }
      }
    </board>
}
