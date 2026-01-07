package aview.gui

import controller.controllerComponent.ControllerAPI
import scalafx.application.{JFXApp3, Platform}
import scalafx.scene.Scene
import scalafx.scene.layout.{BorderPane, GridPane, HBox, VBox}
import scalafx.scene.control.{Button, Label, Menu, MenuBar, MenuItem, ScrollPane}
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.paint.Color
import scalafx.scene.text.Font
import scalafx.scene.effect.DropShadow
import scalafx.Includes._

import util.Observer
import controller.controllerComponent.GameStatus
import model.*
import util.HintSystem


case class GameScene(gui: GUI, controller: ControllerAPI, levelIndex: Int)
  extends Observer:

  controller.add(this)

  private val grid = new GridPane()

  private val statusLabel = new Label("Willkommen zu Memory!") {
    font = Font("Arial", 18)
    textFill = Color.DarkBlue
  }

  private val playerLabel = new Label("Spieler: Mensch") {
    font = Font("Arial", 14)
    textFill = Color.DarkGreen
  }

  private val levelLabel = new Label(s"Level: ${levelIndex + 1}") {
    font = Font("Arial", 14)
    textFill = Color.DarkRed
  }

  val scene: Scene = new Scene {
    root = new BorderPane {

      // ---------------- MENU OBEN ----------------
      top = buildMenu()

      // ---------------- CENTER (ScrollPane) ----------------
      center = new ScrollPane {
        fitToWidth = true
        content = buildBoardWithCoordinates()
      }

      // ---------------- STATUS UNTEN ----------------
      bottom = buildStatusBar()
    }
  }

  drawBoard()
  updateStatus()

  // ---------------------------------------------------------
  // MENU
  // ---------------------------------------------------------

  private def buildMenu(): MenuBar =
    new MenuBar {
      menus = List(
        new Menu("Spiel") {
          items = List(
            new MenuItem("Hint anzeigen") {
              onAction = _ => controller.processInput("hint")
            },
            new MenuItem("Rückgängig (Undo)") {
              onAction = _ => controller.undo()
            },
            new MenuItem("Wiederholen (Redo)") {
              onAction = _ => controller.redo()
            },
            new MenuItem("Levelübersicht") {
              onAction = _ => gui.showLevelSelect()
            },
            new MenuItem("Menü") {
              onAction = _ => gui.showStartMenu()
            },
            new MenuItem("Beenden") {
              onAction = _ => Platform.exit()
            }
          )
        }
      )
    }

  // ---------------------------------------------------------
  // STATUS
  // ---------------------------------------------------------

  private def buildStatusBar(): VBox =
    new VBox {
      spacing = 5
      padding = Insets(10)
      alignment = Pos.CenterLeft
      maxWidth = 600

      children = Seq(
        statusLabel,
        new HBox {
          spacing = 20
          children = Seq(playerLabel, levelLabel)
        }
      )
    }

  // ---------------------------------------------------------
  // BOARD + KOORDINATEN
  // ---------------------------------------------------------

  private def buildBoardWithCoordinates(): VBox =
    val b = controller.board
    val (rows, cols) = calculateBoardSize(b)

    val columnLabels = new HBox {
      spacing = 10
      alignment = Pos.Center
      maxWidth = 600
      children = (0 until cols).map(c => new Label(c.toString) {
        font = Font("Arial", 18)
        textFill = Color.DarkGray
      })
    }

    val rowLabels = new VBox {
      spacing = 10
      alignment = Pos.Center
      children = (0 until rows).map(r => new Label(r.toString) {
        font = Font("Arial", 18)
        textFill = Color.DarkGray
      })
    }

    new VBox {
      spacing = 10
      alignment = Pos.Center
      padding = Insets(20)
      maxWidth = 600
      children = Seq(
        columnLabels,
        new HBox {
          spacing = 10
          alignment = Pos.Center
          children = Seq(rowLabels, grid)
        }
      )
    }

  // ---------------------------------------------------------
  // BOARD ZEICHNEN
  // ---------------------------------------------------------

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
            maxWidth = Double.MaxValue
            minWidth = 100
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

  // ---------------------------------------------------------
  // BOARD-GRÖSSE (KORREKT!)
  // ---------------------------------------------------------

  private def calculateBoardSize(board: Board): (Int, Int) =
    val total = board.cards.length
    val side = math.sqrt(total).toInt
    (side, side)

  // ---------------------------------------------------------
  // STATUS UPDATE
  // ---------------------------------------------------------

  private def updateStatus(): Unit =
    val msg = GameStatus.message(controller.gameStatus)
    if msg.nonEmpty then statusLabel.text = msg

    playerLabel.text =
      s"Spieler: ${if controller.currentPlayer == "human" then "Mensch" else "KI"}"

    val lvl = controller.game.currentLevelIndex + 1
    levelLabel.text = s"Level: $lvl"

  // ---------------------------------------------------------
  // OBSERVER
  // ---------------------------------------------------------

  override def update: Boolean =
    Platform.runLater {
      drawBoard()
      updateStatus()
    }
    true
