package aview.gui

import scalafx.scene.layout.{BorderPane, VBox, HBox, FlowPane, Region, Priority}
import scalafx.scene.control.{Button, Label}
import scalafx.scene.text.Font
import scalafx.geometry.{Insets, Pos}

case class Auswahl(gui: GUI):

  private var selectedTheme = "fruits"
  private var selectedAI = "none"

  private val themeButtons = scala.collection.mutable.Map.empty[String, Button]
  private val aiButtons = scala.collection.mutable.Map.empty[String, Button]

  private def option(selected: Boolean, base: String, active: String) =
    s"""
    -fx-background-radius: 16;
    -fx-padding: 12 32;
    -fx-font-size: 16px;
    -fx-text-fill: white;
    -fx-background-color: ${if selected then active else base};
    -fx-border-color: white;
    -fx-border-width: ${if selected then 3 else 0};
    """

  val root = new BorderPane {
    style = "-fx-background-color: #f7f9fb;"

    center = new VBox {
      spacing = 35
      padding = Insets(40)
      alignment = Pos.Center

      children = Seq(
        new Label("Theme auswählen") { font = Font("Arial", 26) },
        buildThemeButtons(),
        new Label("AI auswählen") { font = Font("Arial", 26) },
        buildAIButtons()
      )
    }

    bottom = new HBox {
      padding = Insets(20)
      alignment = Pos.Center

      val spacer = new Region()
      HBox.setHgrow(spacer, Priority.Always)

      children = Seq(
        new Button("⟵ Zurück") {
          onAction = _ => gui.showStartMenu()
        },
        spacer,
        new Button("Weiter ⟶") {
          style = "-fx-background-color:#2ecc71;-fx-text-fill:white;-fx-padding:14 36;"
          onAction = _ =>
            gui.setTheme(selectedTheme)
            gui.setAI(selectedAI)
            gui.showGame(0)
        }
      )
    }
  }

  private def buildThemeButtons(): FlowPane =
    val themes = Seq("fruits", "animals", "emoji", "sports")
    new FlowPane {
      hgap = 22
      alignment = Pos.Center
      children = themes.map { t =>
        val b = new Button(t.capitalize) {
          style = option(t == selectedTheme, "#5dade2", "#2e86c1")
          onAction = _ =>
            selectedTheme = t
            updateThemes()
        }
        themeButtons(t) = b
        b
      }
    }

  private def updateThemes(): Unit =
    themeButtons.foreach { (n, b) =>
      b.style = option(n == selectedTheme, "#5dade2", "#2e86c1")
    }

  private def buildAIButtons(): FlowPane =
    val ais = Seq("none", "easy", "medium", "hard")
    new FlowPane {
      hgap = 22
      alignment = Pos.Center
      children = ais.map { a =>
        val b = new Button(a.capitalize) {
          style = option(a == selectedAI, "#af7ac5", "#8e44ad")
          onAction = _ =>
            selectedAI = a
            updateAI()
        }
        aiButtons(a) = b
        b
      }
    }

  private def updateAI(): Unit =
    aiButtons.foreach { (n, b) =>
      b.style = option(n == selectedAI, "#af7ac5", "#8e44ad")
    }
