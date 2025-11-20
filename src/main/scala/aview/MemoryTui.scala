package aview

import controller.Controller
import util.Observer
import scala.io.StdIn.readLine

class MemoryTui(controller: Controller) extends Observer:

  controller.add(this)

  def run(): Unit =
    println(s"🎮 Memory gestartet! (${controller.game.rows} x ${controller.game.cols})\n")

    //Zeige zu Beginn das Board an:
    controller.notifyObservers()

    while (!controller.board.allMatched) do
      println(s"Wähle eine Karte (0 bis ${controller.board.cards.size - 1}):")
      val input = readLine()

      val continue = controller.processInput(input)

      if !continue then return   // <<< HARTE ABBRUCH-KONTROLLE

    println("Alle Paare gefunden! Du hast gewonnen! 🎉")

  override def update(): Unit =
    println(boardToString)

  def boardToString: String =
    val rows = controller.game.rows
    val cols = controller.game.cols
    val b = controller.board

    (0 until rows).map { r =>
      (0 until cols).map { c =>
        val i = r * cols + c
        val card = b.cards(i)

        if card.isMatched then "[✅]"
        else if card.isFaceUp then s"[${card.symbol}]"
        else "[ ]"
      }.mkString(" ")
    }.mkString("\n")
