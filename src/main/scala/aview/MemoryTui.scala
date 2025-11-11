package aview

import controller.Controller
import util.Observer
import scala.io.StdIn.readLine
import scala.util.Try


case class MemoryTui(controller: Controller) extends Observer:

  controller.add(this)

  def run(): Unit = 
    println(s"🎮 Memory gestartet!\n")

    while !controller.isFinished do
      showBoard()
      println("Wähle eine Karte (0 bis " + (controller.getBoard.cards.size - 1) + "):")
      val input = readLine()

      // Abbruch mit ^D, ^Z oder leerer Eingabe
      if input == null || input.trim.isEmpty then
      println("\n Spiel beendet durch Eingabeabbruch. Bye👋")
      return
    
      Try(input.toInt).toOption match
        case Some(i) => controller.chooseCard(i)
        case None    => println("❗ Ungültige Eingabe.")

    println("Alle Paare gefunden! Du hast gewonnen! 🎉")

  def update: Unit =
    controller.getLastResult match
      case Some(true)  => println("✅ Treffer!")
      case Some(false) => println("❌ Kein Treffer.")
      case None        => println("zweite Karte wählen...")

  def showBoard(): Unit =
    val board = controller.getBoard
    val cards = board.cards
    val size = math.sqrt(cards.size).toInt
    for (r <- 0 until size)
      println((0 until size).map { c =>
        val i = r * size + c
        val card = cards(i)
        if card.isMatched then "[✅]"
        else if card.isFaceUp then s"[${card.symbol}]"
        else "[ ]"
      }.mkString(" "))