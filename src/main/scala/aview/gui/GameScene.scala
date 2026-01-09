package aview.gui

import controller.controllerComponent.{ControllerAPI, GameStatus}
import scalafx.scene.layout.{BorderPane, GridPane, VBox, HBox}
import scalafx.scene.control.{Button, Label}
import scalafx.scene.text.Font
import scalafx.scene.paint.Color
import scalafx.geometry.{Insets, Pos}
import scalafx.application.Platform
import scalafx.Includes._
import util.HintSystem

case class GameScene(gui: GUI, controller: ControllerAPI, levelIndex: Int):

  // UI ELEMENTE ----------------

  private val grid = new GridPane()

  private val statusLabel = new Label() {
    font = Font("Arial", 16)
    textFill = Color.Black
  }

  private val playerLabel = new Label() {
    font = Font("Arial", 14)
  }

  private val levelLabel = new Label(s"Level: ${levelIndex + 1}") {
    font = Font("Arial", 14)
  }

  // ROOT ----------------

  val root: BorderPane = new BorderPane {
    style = "-fx-background-color: #f7f9fb;"

    top = buildTopBar()
    center = buildCenter()
    bottom = buildStatusBar()
  }

  // initial
  redrawBoard()
  updateStatus()

  // TOP BAR--------------------------

  private def buildTopBar(): HBox =
    new HBox {
      spacing = 10
      padding = Insets(10)
      alignment = Pos.CenterLeft
      style = "-fx-background-color: #ecf0f1;"

      children = Seq(
        new Button("⟵ Menü") {
          onAction = _ => gui.showStartMenu()
        },
        new Button("Undo") {
          onAction = _ => controller.undo()
        },
        new Button("Redo") {
          onAction = _ => controller.redo()
        },
        new Button("Hint") {
          onAction = _ => showHint()
        }
      )
    }

  // CENTER----------------------------------

  private def buildCenter(): VBox =
    new VBox {
      alignment = Pos.Center
      padding = Insets(20)
      children = Seq(grid)
    }

  // STATUS BAR------------------------------

  private def buildStatusBar(): VBox =
    new VBox {
      spacing = 6
      padding = Insets(10)
      children = Seq(
        statusLabel,
        new HBox {
          spacing = 20
          children = Seq(playerLabel, levelLabel)
        }
      )
    }

  // BOARD ZEICHNEN-------------------------------

  def redrawBoard(): Unit =
    // 🔑 UI liest STATE, nicht Component
    val b = controller.board.board
    val total = b.cards.length
    val cols = math.ceil(math.sqrt(total)).toInt

    grid.children.clear()
    grid.hgap = 16
    grid.vgap = 16
    grid.alignment = Pos.Center

    for i <- 0 until total do
      val card = b.cards(i)

      val btn = new Button {
        prefWidth = 120
        prefHeight = 140
        font = Font("Arial", 28)

        text =
          if card.isMatched then "✓"
          else if card.isFaceUp then card.symbol
          else "?"

        style =
          if card.isMatched then "-fx-background-color: #2ecc71;"
          else if card.isFaceUp then "-fx-background-color: #f9e79f;"
          else "-fx-background-color: #5dade2;"

        onAction = _ =>
          if controller.currentPlayer == "human"
             && !card.isFaceUp
             && !card.isMatched
          then
            controller.processInput(i.toString)
      }

      GridPane.setRowIndex(btn, i / cols)
      GridPane.setColumnIndex(btn, i % cols)
      grid.children.add(btn)

  // STATUS UPDATE -----------------------

  def updateStatus(): Unit =
    controller.gameStatus match
      case GameStatus.InvalidSelection(i) if i == -1 =>
        () // Hint / ungültige Texte ignorieren

      case other =>
        val msg = GameStatus.message(other)
        if msg.nonEmpty then
          statusLabel.text = msg

    playerLabel.text =
      s"Spieler: ${if controller.currentPlayer == "human" then "Mensch" else "KI"}"

  // HINT (wie TUI)-------------------------

  private def showHint(): Unit =
    // 🔑 Hint arbeitet auf STATE
    HintSystem.getHint(controller.board.board) match
      case Some((a, b)) =>
        statusLabel.text =
          s"💡 Hinweis: Sicheres Paar → Karte $a und Karte $b!"
      case None =>
        statusLabel.text =
          "💡 Kein sicheres Paar bekannt."
