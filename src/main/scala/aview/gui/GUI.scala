package aview.gui

import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.stage.Stage
import controller.controllerComponent.ControllerAPI
import aview.gui.{StartMenu, Auswahl, LevelCard, GameScene}
import scalafx.application.JFXApp3.PrimaryStage


class GUI(val controller: ControllerAPI) extends JFXApp3:

  // Level 1 freigeschaltet, danach dynamisch erweitern
  private var unlockedLevels: Set[Int] = Set(0) // 0-basiert: Level-Index 0 = erstes Level

  override def start(): Unit =
    stage = new PrimaryStage()
    stage.title = "Memory"
    stage.width = 900
    stage.height = 750
    stage.resizable = true
    showStartMenu()

  def showStartMenu(): Unit =
    stage.scene = StartMenu(this).scene

  // dein Auswahl-Screen für Theme + AI
  def showMöglichkeiten(): Unit =
    stage.scene = Auswahl(this).scene

  def showLevelSelect(): Unit =
    stage.scene = LevelCard(this, unlockedLevels).scene

  def showGame(levelIndex: Int): Unit =
    // Wenn du später Level-spezifisches Laden willst, kannst du hier controller.loadLevel(levelIndex) etc. machen
    stage.scene = GameScene(this, controller, levelIndex).scene

  def unlockLevel(levelIndex: Int): Unit =
    unlockedLevels += levelIndex

  // von Auswahl/Settings aufgerufen
  def setTheme(name: String): Unit =
    controller.game.setTheme(name)

  def setAI(name: String): Unit =
    controller.game.setAI(name)
