import com.google.inject.Guice
import scalafx.application.Platform
import scala.io.StdIn.readLine

import aview.MemoryTui
import aview.gui.GUI
import controller.controllerComponent.ControllerAPI
import model._
import model.modelComponent.implModel.MemoryGameImpl

object Memory {

  def main(args: Array[String]): Unit = {

    println("Welcome to Memory!")

    // 1) Mode
    println("choose the mode:")
    println("  1) just TUI")
    println("  2) just GUI")
    println("  3) both")
    val mode = readLine().trim match
      case "2" => "gui"
      case "3" => "both"
      case _   => "tui"

    // 2) Theme
    // Theme & AI nur bei TUI abfragen
    val (theme, ai) =
      if mode == "tui" then
        println("Choose theme: fruits / animals / emoji / sports / vehicles / flags / landscape")
        val themeName = readLine().trim
        val theme = ThemeFactory.getTheme(
          if themeName.nonEmpty then themeName else "fruits"
        )

        println("Choose AI level: none / easy / medium / hard / pro")
        val ai = readLine().trim.toLowerCase match
          case "none"   => NoAI()
          case "easy"   => RandomAI()
          case "medium" => MediumAI()
          case "hard"   => HardAI()
          case "pro"    => MemoryAI()
          case _        => RandomAI()

        (theme, ai)
      else
        // GUI setzt Theme & AI später
        (ThemeFactory.getTheme("fruits"), RandomAI())

    // 3) Levels
    val levels = Vector(
      Level(BoardSizes.Small2x2, Difficulties.Easy),
      Level(BoardSizes.Medium4x4, Difficulties.Easy),
      Level(BoardSizes.Medium4x4, Difficulties.Hard),
      Level(BoardSizes.Large6x6, Difficulties.Easy)
    )

    // 4) Dependency Injection
    val injector = Guice.createInjector(new MemoryModule(theme, ai, levels))
    val controller = injector.getInstance(classOf[ControllerAPI])


    // 5) Views
    mode match
      case "gui" =>
        Platform.startup(() => {})
        new GUI(controller).main(Array())

      case "both" =>
        // GUI im eigenen Thread (WICHTIG!)
        new Thread(() => {
          Platform.startup(() => {})
          new GUI(controller).main(Array())
        }).start()

        // TUI im Hauptthread
        MemoryTui(controller).run()

      case _ =>
        MemoryTui(controller).run()
  }
}