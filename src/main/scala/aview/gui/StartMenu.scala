package aview.gui

import scalafx.scene.layout.VBox
import scalafx.scene.control.{Button}
import scalafx.scene.image.{Image, ImageView}
import scalafx.geometry.Pos

case class StartMenu(gui: GUI):

  // LOGO als Bild (statt "Memory"-Text)
  private val logo = new ImageView(
    new Image(getClass.getResource("/startmenu/MemoryStart.png").toString)
  ) {
    fitWidth = 520
    preserveRatio = true
  }

  // play-BUTTON BILD
  private val startButtonImage = new ImageView(
    new Image(getClass.getResource("/startmenu/PlayButtons.png").toString)
  ) {
    fitWidth = 230
    preserveRatio = true
  }

  // play-BUTTON (Funktion BLEIBT GLEICH)
  private val startButton = new Button {
    graphic = startButtonImage
    style = "-fx-background-color: transparent;"
    onAction = _ => gui.showMöglichkeiten()
  }

  // EXIT-BUTTON
  private val exitButtonImage = new ImageView(
    new Image(getClass.getResource("/startmenu/ExitButtons.png").toString)
  ) {
    fitWidth = 200
    preserveRatio = true
  }

  private val exitButton = new Button {
    graphic = exitButtonImage
    style = "-fx-background-color: transparent;"
    onAction = _ => System.exit(0)
  }

  val root = new VBox {
    spacing = 30
    alignment = Pos.Center
    style = "-fx-background-color: #d6eaff;"

    children = Seq(
      logo,
      startButton,
      exitButton
    )
  }