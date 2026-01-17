package aview.gui

import scalafx.application.{JFXApp3, Platform}
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.layout.BorderPane
import controller.controllerComponent.{ControllerAPI, GameStatus}
import util.Observer

class GUI(val controller: ControllerAPI) extends JFXApp3 with Observer:

  private val rootPane = new BorderPane()
  private var currentGameScene: Option[GameScene] = None
  @volatile private var aiRunning = false

  override def start(): Unit =
    controller.add(this)

    stage = new PrimaryStage()
    stage.title = "Memory"
    stage.scene = new Scene(rootPane, 600, 600)
    stage.setResizable(true)

    showStartMenu()

  // ---------- Navigation ----------

  def showStartMenu(): Unit =
    currentGameScene = None
    rootPane.center = StartMenu(this).root
    rootPane.top = null
    rootPane.bottom = null

  def showMöglichkeiten(): Unit =
    currentGameScene = None
    rootPane.center = Auswahl(this).root
    rootPane.top = null
    rootPane.bottom = null

  def showGame(levelIndex: Int): Unit =
    val gs = GameScene(this, controller, levelIndex)
    currentGameScene = Some(gs)
    rootPane.center = gs.root
    rootPane.top = null
    rootPane.bottom = null

  // ---------- Settings ----------

  def setTheme(name: String): Unit =
    controller.game.setTheme(name)

  def setAI(name: String): Unit =
    controller.game.setAI(name)

  // ---------- Observer ----------

  override def update: Boolean =
    Platform.runLater {
      currentGameScene.foreach { gs =>
        gs.redrawBoard()
        gs.updateStatus()
      }
      runAI()
    }
    true

  // ---------- AI ----------

  private def runAI(): Unit =
    val status = controller.gameStatus

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
