package util

import model.{Board, Card}

// ------------------------------------------------------
// TEMPLATE METHOD BASE CLASS
// ------------------------------------------------------
trait BoardRenderer:

  // TEMPLATE METHOD → TUI ruft NUR diese Methode auf
  final def render(board: Board): String =
    val (rows, cols) = computeGrid(board)
    val matrix       = buildMatrix(board, rows, cols)
    formatMatrix(matrix)

  // ----------------------------
  // GRID-BERECHNUNG (unterstützt 2×3, 3×3, 4×3, 4×5 usw.)
  // ----------------------------
  protected def computeGrid(board: Board): (Int, Int) =
    val total = board.cards.size
    val root  = math.sqrt(total).toInt

    // größten Divisor ≤ sqrt(total) finden
    val bestRow =
      (1 to root).reverse.find(r => total % r == 0).getOrElse(1)

    val bestCol = total / bestRow

    (bestRow, bestCol)


  // --------------------------------------------------
  // WIE EINE EINZELNE KARTE GERENDET WIRD
  // --------------------------------------------------
  protected def renderCard(card: Card): String =
    if card.isMatched then "[✅]"
    else if card.isFaceUp then s"[${card.symbol}]"
    else "[ ]"

  // --------------------------------------------------
  // MATRIX AUS KARTEN BAUEN
  // --------------------------------------------------
  private def buildMatrix(board: Board, rows: Int, cols: Int): Vector[Vector[String]] =
    (0 until rows).map { r =>
      (0 until cols).map { c =>
        val i = r * cols + c
        if i < board.cards.size then
          renderCard(board.cards(i))
        else
          "   " // leere Plätze falls Grid größer (eigentlich nie nötig)
      }.toVector
    }.toVector

  // --------------------------------------------------
  // FORMATIERUNG (Standard ASCII Layout)
  // --------------------------------------------------
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