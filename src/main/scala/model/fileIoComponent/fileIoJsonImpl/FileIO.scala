package model.fileIoComponent.fileIoJsonImpl

import model.fileIoComponent.FileIOInterface
import model.{Board, Card}

import play.api.libs.json._
import scala.io.Source

class FileIO extends FileIOInterface {

  override def load: Board = {
    val source = Source.fromFile("memory.json").getLines.mkString
    val json = Json.parse(source)

    val cards = (json \ "cards").as[Seq[JsValue]].map { c =>
      Card(
        id        = (c \ "id").as[Int],
        symbol    = (c \ "symbol").as[String],
        isFaceUp  = (c \ "isFaceUp").as[Boolean],
        isMatched = (c \ "isMatched").as[Boolean]
      )
    }.toVector

    Board(cards)
  }

  override def save(board: Board): Unit = {
    import java.io._
    val pw = new PrintWriter(new File("memory.json"))
    pw.write(Json.prettyPrint(boardToJson(board)))
    pw.close()
  }

  private def boardToJson(board: Board): JsObject =
    Json.obj(
      "cards" -> Json.toJson(
        board.cards.map { c =>
          Json.obj(
            "id"        -> c.id,
            "symbol"    -> c.symbol,
            "isFaceUp"  -> c.isFaceUp,
            "isMatched" -> c.isMatched
          )
        }
      )
    )
}
