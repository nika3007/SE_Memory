package aview.gui

import scalafx.scene.layout.VBox
import scalafx.scene.control.{Button, Label}
import scalafx.scene.text.Font
import scalafx.geometry.Pos

case class StartMenu(gui: GUI):

  val root = new VBox {
    spacing = 30
    alignment = Pos.Center
    style = "-fx-background-color: #f7f9fb;"

    children = Seq(
      new Label("Memory") {
        font = Font("Arial", 42)
      },
      new Button("Start") {
        style = "-fx-background-radius:16;-fx-padding:14 40;-fx-font-size:20;"
        onAction = _ => gui.showMöglichkeiten()
      },
      new Button("Beenden") {
        style = "-fx-background-radius:16;-fx-padding:14 40;-fx-font-size:20;"
        onAction = _ => System.exit(0)
      }
    )
  }
