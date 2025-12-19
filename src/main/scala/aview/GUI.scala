package aview

import controller.ControllerAPI
import scalafx.application.{JFXApp3, Platform}
import scalafx.scene.Scene
import scalafx.scene.layout.{BorderPane, GridPane, HBox, VBox}
import scalafx.scene.control.{Button, Label, Menu, MenuBar, MenuItem}
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.paint.Color
import scalafx.scene.text.Font
import scalafx.scene.effect.DropShadow
import scalafx.Includes._

import controller.Controller
import util.Observer
import controller.GameStatus
import model.*
import util.HintSystem


class GUI(val controller: ControllerAPI) extends JFXApp3 with Observer:

  println("[GUI] Constructor called - adding as observer")
  controller.add(this)

  // GUI-Komponenten
  private val grid = new GridPane()
  private val statusLabel = new Label("Willkommen zu Memory!") {
    font = Font("Arial", 18)
    textFill = Color.DarkBlue
  }
  private val playerLabel = new Label("Spieler: Mensch") {
    font = Font("Arial", 14)
    textFill = Color.DarkGreen
  }
  private val levelLabel = new Label("Level: 1") {
    font = Font("Arial", 14)
    textFill = Color.DarkRed
  }

  override def start(): Unit =
    stage = new JFXApp3.PrimaryStage {
      title = "Memory – ScalaFX"
      width = 900
      height = 750

      scene = new Scene {
        root = buildRoot()
      }
    }

    // Initiales Board zeichnen
    drawBoard()
    updateStatus()

  // --------------- UI ROOT ---------------------------------------------
  private def buildRoot(): BorderPane =
    val root = new BorderPane()
    root.top = buildMenu()
    root.center = grid
    root.bottom = buildStatusBar()
    root

  private def buildMenu(): MenuBar =
    new MenuBar {
      menus = List(
        new Menu("Spiel") {
          items = List(
            new MenuItem("Hint anzeigen") {
              onAction = _ => showHint()
            },
            new MenuItem("Rückgängig (Undo)") {
              onAction = _ =>
                controller.undo()
                drawBoard()
            },
            new MenuItem("Beenden") {
              onAction = _ => Platform.exit()
            }
          )
        }
      )
    }

  private def buildStatusBar(): VBox =
    new VBox {
      spacing = 5
      padding = Insets(10)
      alignment = Pos.CenterLeft

      children = Seq(
        statusLabel,
        new HBox {
          spacing = 20
          children = Seq(playerLabel, levelLabel)
        }
      )
    }

  // --------------- BOARD ZEICHNEN ----------------------------------------
  private def drawBoard(): Unit =
    println(s"[GUI drawBoard] Called")
    try {
      val b = controller.board
      // Hole Board-Größe über eine Hilfsfunktion
      val (rows, cols) = calculateBoardSize(b)

      println(s"[GUI drawBoard] Board size: $rows x $cols, Cards: ${b.cards.length}")
      println(s"[GUI drawBoard] Matched cards: ${b.cards.count(_.isMatched)}/${b.cards.length}")

      grid.children.clear()
      grid.hgap = 10
      grid.vgap = 10
      grid.padding = Insets(20)
      grid.alignment = Pos.Center

      for (r <- 0 until rows) do
        for (c <- 0 until cols) do
          val i = r * cols + c
          if i < b.cards.length then
            val card = b.cards(i)

            val button = new Button {
              prefWidth = 100
              prefHeight = 120
              font = Font("Arial", 24)

              // Stil basierend auf Kartenstatus
              if card.isMatched then
                style = "-fx-background-color: #90EE90; -fx-border-color: #228B22; -fx-border-width: 3;"
                text = "✓ " + card.symbol
                textFill = Color.DarkGreen
              else if card.isFaceUp then
                style = "-fx-background-color: #FFFACD; -fx-border-color: #DAA520; -fx-border-width: 2;"
                text = card.symbol
                textFill = Color.Black
              else
                style = "-fx-background-color: linear-gradient(to bottom, #4169E1, #00008B); -fx-border-color: #191970; -fx-border-width: 2;"
                text = "?"
                textFill = Color.White

              // Event-Handler (vereinfacht ohne implizite Parameter)
              onAction = handle {
                println(s"[GUI] Button clicked: index=$i, card matched=${card.isMatched}, faceUp=${card.isFaceUp}")
                if controller.currentPlayer == "human" && !card.isMatched && !card.isFaceUp then
                  controller.processInput(i.toString)
              }
            }

            // Schatten-Effekt
            button.effect = new DropShadow {
              radius = 5
              offsetX = 3
              offsetY = 3
              color = Color.Gray
            }

            GridPane.setRowIndex(button, r)
            GridPane.setColumnIndex(button, c)
            grid.children.add(button)
    } catch {
      case e: Exception =>
        println(s"[GUI ERROR in drawBoard] $e")
        e.printStackTrace()
        statusLabel.text = s"Fehler beim Zeichnen: ${e.getMessage}"
    }

  // Hilfsfunktion zur Berechnung der Board-Größe
  private def calculateBoardSize(board: Board): (Int, Int) =
    val totalCards = board.cards.length
    // Annahme: Board ist immer rechteckig, versuche typische Größen
    if totalCards <= 4 then (2, 2)        // 2x2
    else if totalCards <= 16 then (4, 4)  // 4x4
    else if totalCards <= 36 then (6, 6)  // 6x6
    else (8, 8)                           // Fallback

  // --------------- STATUS AKTUALISIEREN --------------------------------
  private def updateStatus(): Unit =
    println(s"[GUI updateStatus] Called")
    try {
      println(s"[GUI updateStatus] GameStatus: ${controller.gameStatus}")
      val allMatched = controller.board.cards.forall(_.isMatched)
      println(s"[GUI updateStatus] All matched? $allMatched")

      // Statusmeldung
      val msg = GameStatus.message(controller.gameStatus)
      if msg.nonEmpty then
        statusLabel.text = msg

      // Spieler-Info
      playerLabel.text = s"Spieler: ${if controller.currentPlayer == "human" then "Mensch" else "KI"}"

      // Level-Info - Versuche Level-Nummer zu ermitteln
      val currentLevelNumber = controller.game.currentLevelNumber
      levelLabel.text = s"Level: $currentLevelNumber"
      stage.title = s"Memory – Level $currentLevelNumber"

      levelLabel.text = s"Level: $currentLevelNumber"

      // Fenstertitel aktualisieren
      stage.title = s"Memory – Level $currentLevelNumber"

      // Spezielle Effekte für bestimmte Events
      // Prüfe erst welche GameStatus-Werte wirklich existieren
      controller.gameStatus match {
        case status if status.toString.contains("Complete") || status.toString.contains("Level") =>
          println("[GUI] LevelComplete or similar detected!")
          statusLabel.text = "Level geschafft!"
          statusLabel.textFill = Color.Green

          // Warte und zeichne neu
          new Thread(() => {
            Thread.sleep(1500)
            Platform.runLater {
              println("[GUI] Redrawing board...")
              statusLabel.text = "Weiter..."
              statusLabel.textFill = Color.DarkBlue
              drawBoard()
            }
          }).start()

        case status if status.toString.contains("Match") =>
          // Animation für Match
          statusLabel.textFill = Color.Green
          new Thread(() => {
            Thread.sleep(1000)
            Platform.runLater {
              statusLabel.textFill = Color.DarkBlue
            }
          }).start()

        case status if status.toString.contains("NoMatch") =>
          statusLabel.textFill = Color.Red

        case _ =>
          statusLabel.textFill = Color.DarkBlue
      }
    } catch {
      case e: Exception =>
        println(s"[GUI ERROR in updateStatus] $e")
        e.printStackTrace()
        statusLabel.text = s"Status-Fehler: ${e.getMessage}"
    }

  // --------------- HINT SYSTEM -----------------------------------------
  private def showHint(): Unit =
    try {
      HintSystem.getHint(controller.board) match {
        case Some((card1, card2)) =>
          statusLabel.text = s"💡 Tipp: Paar → Karte $card1 und $card2!"
        case None =>
          statusLabel.text = "💡 Kein sicheres Paar bekannt."
      }
    } catch {
      case e: Exception =>
        statusLabel.text = s"Hint-Fehler: ${e.getMessage}"
    }

  // --------------- OBSERVER UPDATE -----------------------------------
  override def update: Boolean =
    println(s"[GUI Observer Update] Called from controller")
    Platform.runLater {
      try {
        println(s"[GUI Update] Status: ${controller.gameStatus}")
        drawBoard()
        updateStatus()
      } catch {
        case e: Exception =>
          println(s"[GUI ERROR in update] $e")
          e.printStackTrace()
      }
    }
    true

end GUI