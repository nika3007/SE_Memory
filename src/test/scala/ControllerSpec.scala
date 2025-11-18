package controller //spec und der eigentliche controller leben in einem package

import model.{Board, Card, MemoryGame}
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

final class ControllerSpec extends AnyWordSpec with Matchers { //final --> klasse wird nicht weiter ausgebaut

  //hilfsfunktion, kleines board, um randomness zu vermeiden-> fürs testen
  private def smallBoard(): Board =
    Board(Vector(
      Card(0, "A"), // pair A
      Card(1, "A"),
      Card(2, "B"), // pair B
      Card(3, "B")
    ))

  private def freshControllerWithBoard(): Controller = // kleiner privater hilfscontroller zum testen mit fixierten werten
    val c = new Controller(2, 2)
    //überschreibe das eigentliche random board hiermit für testzwecke
    c.game.board = smallBoard()
    c

  "A Controller" should {

    "stop the game when input is null or empty" in { // das spiel sollte sofort unterbrochen werden beu null oder leerem input
      val c = freshControllerWithBoard()
      val before = c.board

      c.processInput(null) shouldBe false
      c.board shouldBe before // keine veränderung ignoriere falschen input

      c.processInput("   ") shouldBe false
      c.board shouldBe before // "  "
    }

    "reject non-numeric input and keep the board unchanged" in { // jeglicher input der keine zahl ist wird ignoriert aber das board bleibt unverändert
      val c = freshControllerWithBoard()
      val before = c.board

      val result = c.processInput("abc") // input der keine zahl ist wird auch ignoriert board unverändert und spiel läft normal weiter
      //printe eine error message aber spiel läuft weiter

      result shouldBe true          // spiel läuft weiter
      c.board shouldBe before       // board unverändert
    }

    "reject out-of-range indices and keep the board unchanged" in { // indexe bzw zahlen die out of range sind werden ignoriert und spiel läuft weiter
      val c = freshControllerWithBoard()
      val before = c.board

      c.processInput("-1") shouldBe true // ignoriere werte die folgende grenzen übersteigen
      c.processInput("99") shouldBe true
      // printe error message aber das spiel läuft dennoch normal weiter

      c.board shouldBe before
    }

    "flip a valid first card and not resolve yet" in { // dreht eine gültige karte um aber "löst" das spiel nicht auf
      val c = freshControllerWithBoard()
      val before = c.board

      val ok = c.processInput("0") // erste gültige umgedrehte karte
      ok shouldBe true

      val after = c.board
      after.cards(0).isFaceUp shouldBe true           // karte 0 aufgedeckt, alle anderen aber nicht, warten auf aufdeckung
      after.cards(1).isFaceUp shouldBe false
      after.cards(2).isFaceUp shouldBe false
      after.cards(3).isFaceUp shouldBe false

      // boar hat sich durch das umdrehen einer validen karte verändert --> board soll geupdatet werden un ist nicht wie davor
      after should not be theSameInstanceAs(before)
    }

    "mark a pair as matched when two matching cards are chosen" in { // zwei passende bzw gleiche karten werden als ein match markiert
      val c = freshControllerWithBoard()

      // erste Karte A
      c.processInput("0") shouldBe true // erste karte aufdecken (gültig)
      // zweite Karte A
      c.processInput("1") shouldBe true // zweite karte aufdecken (auch gültig)

      val b2 = c.board
      b2.cards(0).isMatched shouldBe true // beide karten sind gleich also ein match und merden dementsprechend markiert
      b2.cards(1).isMatched shouldBe true
    }

    "flip back non-matching cards after a failed second choice" in { // drehe ungleiche karten wieder um da sie kein match sind
      val c = freshControllerWithBoard()

      // erste karte
      c.processInput("0") shouldBe true // gültige erste karte wird umgedrehrt und gültige zweite karte auch
      // zweite karte ist kein match
      c.processInput("2") shouldBe true

      // karten werden bei keinem match wieder umgedreht und board wird vorerst seit dem letzen stand unverändert zurückgegeben
      val b3 = c.board
      b3.cards(0).isFaceUp shouldBe false
      b3.cards(2).isFaceUp shouldBe false
    }
  }
}
