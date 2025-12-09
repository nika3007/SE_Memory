package util

import model.{Board, Card}

// ------------------------------------------------------
// TEMPLATE METHOD BASE CLASS
// ------------------------------------------------------
trait BoardRenderer:

  // TEMPLATE METHOD → TUI ruft NUR diese Methode auf
  final def render(board: Board): String =
    val rows     = computeRows(board)
    val cols     = computeCols(board)
    val matrix   = buildMatrix(board, rows, cols)
    formatMatrix(matrix)

  // ----------------------------
  // HOOKS — können überschrieben werden
  // ----------------------------
  def computeRows(board: Board): Int =
    math.sqrt(board.cards.size).toInt

  def computeCols(board: Board): Int =
    computeRows(board)

  protected def renderCard(card: Card): String =
    if card.isMatched then "[✅]"
    else if card.isFaceUp then s"[${card.symbol}]"
    else "[ ]"

  // ----------------------------
  // FIXED STEPS (nicht überschreiben)
  // ----------------------------
  private def buildMatrix(board: Board, rows: Int, cols: Int): Vector[Vector[String]] =
    (0 until rows).map { r =>
      (0 until cols).map { c =>
        val i = r * cols + c
        if i < board.cards.size then renderCard(board.cards(i))
        else "   "
      }.toVector
    }.toVector

  protected def formatMatrix(matrix: Vector[Vector[String]]): String =
    matrix.map(_.mkString(" ")).mkString("\n")


// ------------------------------------------------------
// DEFAULT IMPLEMENTATION (ASCII RENDERER)
// ------------------------------------------------------
class AsciiRenderer extends BoardRenderer




/* 
BoardRenderer = Schicht, die nur die Darstellung übernimmt
-> somit: TUI = benutzt Renderer, zeigt nur an, macht KEINE Logik = TUI bleibt winzig und lesbar
-> weiterhin: Controller = Spiellogik und Model = Daten (Cards, Board)

=> GUI später möglich


 */