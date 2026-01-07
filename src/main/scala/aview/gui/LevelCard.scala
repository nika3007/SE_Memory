package aview.gui

import scalafx.scene.Scene
import scalafx.scene.layout.{BorderPane, VBox, FlowPane}
import scalafx.scene.control.{Button, Label, Menu, MenuBar, MenuItem, ScrollPane}
import scalafx.scene.text.Font
import scalafx.geometry.{Insets, Pos}

case class LevelCard(gui: GUI, unlockedLevels: Set[Int]):

  private val totalLevels = gui.controller.game.levelsCount

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
            new Label("Level auswählen") {
              font = Font("Arial", 32)
            },
            levelButtons(),

            new Button("Zurück") {
              maxWidth = Double.MaxValue
              minWidth = 200
              prefHeight = 40
              font = Font("Arial", 20)
              style = "-fx-background-color: #95a5a6; -fx-text-fill: white;"
              onAction = _ => gui.showMöglichkeiten()
            }
          )
        }
      }
    }
  }

  private def levelButtons(): FlowPane =
    new FlowPane {
      hgap = 20
      vgap = 20
      alignment = Pos.Center
      maxWidth = 600

      children = (0 until totalLevels).map { idx =>
        val unlocked = unlockedLevels.contains(idx)

        new Button(s"Level ${idx + 1}") {
          maxWidth = Double.MaxValue
          minWidth = 150
          prefHeight = 80
          font = Font("Arial", 22)

          style =
            if unlocked then "-fx-background-color: #2ecc71; -fx-text-fill: white;"
            else "-fx-background-color: #7f8c8d; -fx-text-fill: #bdc3c7;"

          disable = !unlocked

          if unlocked then
            onAction = _ => gui.showGame(idx)
        }
      }
    }
