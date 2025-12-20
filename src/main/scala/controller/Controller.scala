package controller

import controller.controllerComponent.ControllerAPI
import controller.controllerComponent.controllerBaseImpl.ControllerImpl
import model.modelComponent.MemoryGameAPI

object Controller:
  def apply(game: MemoryGameAPI): ControllerAPI =
    new ControllerImpl(game)
