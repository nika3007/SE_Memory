package controller.controllerComponent

import scala.util.Try

trait Command:
  def doStep(): Unit
  def undoStep(): Unit
  def redoStep(): Unit

