package aview.gui

import scalafx.scene.Scene
import scalafx.scene.layout.{BorderPane, VBox}
import scalafx.scene.control.{Button, Label, Menu, MenuBar, MenuItem, ScrollPane}
import scalafx.scene.text.Font
import scalafx.geometry.{Insets, Pos}

case class StartMenu(gui: GUI):

  val scene = new Scene {
    root = new BorderPane {

      top = new MenuBar {
        menus = List(
          new Menu("Spiel") {
            items = List(
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
          spacing = 40
          alignment = Pos.Center
          padding = Insets(60)
          maxWidth = 600

          children = Seq(
            new Label("Memory") {
              font = Font("Arial", 48)
              style = "-fx-text-fill: #2c3e50;"
            },

            new Button("Start") {
              maxWidth = Double.MaxValue
              minWidth = 200
              prefHeight = 60
              font = Font("Arial", 26)
              style = "-fx-background-color: #3498db; -fx-text-fill: white;"
              onAction = _ => gui.showMöglichkeiten()
            },

            new Button("Beenden") {
              maxWidth = Double.MaxValue
              minWidth = 200
              prefHeight = 60
              font = Font("Arial", 26)
              style = "-fx-background-color: #e74c3c; -fx-text-fill: white;"
              onAction = _ => System.exit(0)
            }
          )
        }
      }
    }
  }
