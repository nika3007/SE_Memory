package aview

import controller.controllerComponent.ControllerAPI
import scalafx.application.{JFXApp3, Platform}
import scalafx.scene.Scene
import scalafx.scene.layout.{BorderPane, GridPane, HBox, VBox}
import scalafx.scene.control.{Button, Label, Menu, MenuBar, MenuItem}
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.paint.Color
import scalafx.scene.text.Font
import scalafx.scene.effect.DropShadow
import scalafx.Includes._

import util.Observer
import controller.controllerComponent.GameStatus
import model.*
import util.HintSystem

class GUI(val controller: ControllerAPI) extends JFXApp3 with Observer:

  controller.add(this)

  @volatile private var aiRunning: Boolean = false

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

    drawBoard()
    updateStatus()

  private def buildRoot(): BorderPane =
    new BorderPane {
      top = buildMenu()
      center = grid
      bottom = buildStatusBar()
    }

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

  // ---------------- BOARD --------------------

  private def drawBoard(): Unit =
    val b = controller.board
    val (rows, cols) = calculateBoardSize(b)

    grid.children.clear()
    grid.hgap = 10
    grid.vgap = 10
    grid.padding = Insets(20)
    grid.alignment = Pos.Center

    for r <- 0 until rows do
      for c <- 0 until cols do
        val i = r * cols + c
        if i < b.cards.length then
          val card = b.cards(i)

          val button = new Button {
            prefWidth = 100
            prefHeight = 120
            font = Font("Arial", 24)

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

            onAction = { () =>
              if controller.currentPlayer == "human" && !card.isMatched && !card.isFaceUp then
                controller.processInput(i.toString)
            }
          }

          button.effect = new DropShadow {
            radius = 5
            offsetX = 3
            offsetY = 3
            color = Color.Gray
          }

          GridPane.setRowIndex(button, r)
          GridPane.setColumnIndex(button, c)
          grid.children.add(button)

  private def calculateBoardSize(board: Board): (Int, Int) =
    val total = board.cards.length
    if total <= 4 then (2, 2)
    else if total <= 16 then (4, 4)
    else if total <= 36 then (6, 6)
    else (8, 8)

  // ---------------- STATUS --------------------

  private def updateStatus(): Unit =
    val msg = GameStatus.message(controller.gameStatus)
    if msg.nonEmpty then statusLabel.text = msg

    playerLabel.text =
      s"Spieler: ${if controller.currentPlayer == "human" then "Mensch" else "KI"}"

    val lvl = controller.game.currentLevelIndex + 1
    levelLabel.text = s"Level: $lvl"
    stage.title = s"Memory – Level $lvl"

  // ---------------- HINT --------------------

  private def showHint(): Unit =
    HintSystem.getHint(controller.board) match
      case Some((a, b)) =>
        statusLabel.text = s"💡 Tipp: Paar → Karte $a und $b!"
      case None =>
        statusLabel.text = "💡 Kein sicheres Paar bekannt."

  // ---------------- AI LOGIC --------------------

  private def runAI(): Unit =
    val status = controller.gameStatus

    // AI nur starten, wenn:
    // - AI dran ist
    // - kein AI-Thread gerade läuft
    // - der Status einer von: Idle, NextRound, Match
    if controller.currentPlayer == "ai"
       && !aiRunning
       && (status == GameStatus.Idle
           || status == GameStatus.NextRound
           || status == GameStatus.Match)
    then
      aiRunning = true

      new Thread(() =>
        Thread.sleep(400)
        controller.aiTurnFirst()

        Thread.sleep(700)
        controller.aiTurnSecond()

        aiRunning = false
      ).start()



  // ---------------- OBSERVER --------------------

  override def update: Boolean =
    Platform.runLater {
      drawBoard()
      updateStatus()
      runAI() // <-- AI wird hier gestartet
    }
    true

end GUI
