package aview.gui

import scalafx.scene.Scene
import scalafx.scene.layout.{BorderPane, VBox, HBox}
import scalafx.scene.control.{Button, Label, Menu, MenuBar, MenuItem, ScrollPane}
import scalafx.scene.text.Font
import scalafx.geometry.{Insets, Pos}

case class Auswahl(gui: GUI):

  private var selectedTheme: String = "fruits"
  private var selectedAI: String = "none"

  private val themeButtons = scala.collection.mutable.Map.empty[String, Button]
  private val aiButtons = scala.collection.mutable.Map.empty[String, Button]

  val scene = new Scene {
    root = new BorderPane {

      top = new MenuBar {
        menus = List(
          new Menu("Spiel") {
            items = List(
              new MenuItem("Menü") {
                onAction = _ => gui.showStartMenu()
              },
              new MenuItem("Beenden") {
                onAction = _ => System.exit(0)
              }
            )
          }
        )
      }

      center = new ScrollPane {
        fitToWidth = true

        content = new VBox {
          spacing = 30
          alignment = Pos.Center
          padding = Insets(40)
          maxWidth = 600

          children = Seq(
            new Label("Theme auswählen") {
              font = Font("Arial", 28)
            },
            buildThemeButtons(),

            new Label("AI auswählen") {
              font = Font("Arial", 28)
            },
            buildAIButtons(),

            new VBox {
              spacing = 10
              alignment = Pos.Center

              children = Seq(
                new Button("Weiter") {
                  maxWidth = Double.MaxValue
                  minWidth = 200
                  prefHeight = 60
                  font = Font("Arial", 26)
                  style = "-fx-background-color: #2ecc71; -fx-text-fill: white;"
                  onAction = _ =>
                    gui.setTheme(selectedTheme)
                    gui.setAI(selectedAI)
                    gui.showLevelSelect()
                },

                new Button("Zurück") {
                  maxWidth = Double.MaxValue
                  minWidth = 200
                  prefHeight = 40
                  font = Font("Arial", 20)
                  style = "-fx-background-color: #95a5a6; -fx-text-fill: white;"
                  onAction = _ => gui.showStartMenu()
                }
              )
            }
          )
        }
      }
    }
  }

  private def buildThemeButtons(): HBox =
    val themes = Seq("fruits", "animals", "emoji", "sports", "vehicles", "flags", "landscape")

    new HBox {
      spacing = 10
      alignment = Pos.Center
      maxWidth = 600

      children = themes.map { name =>
        val btn = new Button(name.capitalize) {
          maxWidth = Double.MaxValue
          minWidth = 120
          font = Font("Arial", 18)
          onAction = _ =>
            selectedTheme = name
            updateThemeStyles()
        }
        themeButtons(name) = btn
        btn
      }

      updateThemeStyles()
    }

  private def updateThemeStyles(): Unit =
    themeButtons.foreach { case (name, btn) =>
      if name == selectedTheme then
        btn.style = "-fx-background-color: #2980b9; -fx-text-fill: white; -fx-border-color: white; -fx-border-width: 3;"
      else
        btn.style = "-fx-background-color: #3498db; -fx-text-fill: white;"
    }

  private def buildAIButtons(): HBox =
    val ais = Seq("none", "easy", "medium", "hard", "pro")

    new HBox {
      spacing = 10
      alignment = Pos.Center
      maxWidth = 600

      children = ais.map { name =>
        val btn = new Button(name.capitalize) {
          maxWidth = Double.MaxValue
          minWidth = 120
          font = Font("Arial", 18)
          onAction = _ =>
            selectedAI = name
            updateAIStyles()
        }
        aiButtons(name) = btn
        btn
      }

      updateAIStyles()
    }

  private def updateAIStyles(): Unit =
    aiButtons.foreach { case (name, btn) =>
      if name == selectedAI then
        btn.style = "-fx-background-color: #8e44ad; -fx-text-fill: white; -fx-border-color: white; -fx-border-width: 3;"
      else
        btn.style = "-fx-background-color: #9b59b6; -fx-text-fill: white;"
    }
