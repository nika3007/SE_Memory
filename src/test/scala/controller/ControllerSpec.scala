package controller

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

import controller.controllerComponent.controllerBaseImpl.ControllerImpl
import model.*
import model.modelComponent.implModel.MemoryGameImpl

final class ControllerSpec extends AnyWordSpec with Matchers:

  "Controller.apply" should {

    "create a ControllerImpl with the provided game" in {
      val theme = ThemeFactory.getTheme("emoji")
      val level = Level(BoardSizes.Small2x2, Difficulties.Easy)
      val game = new MemoryGameImpl(theme, NoAI(), Vector(level))

      val controller = Controller(game)

      controller shouldBe a[ControllerImpl]
      controller.game shouldBe game
    }
  }