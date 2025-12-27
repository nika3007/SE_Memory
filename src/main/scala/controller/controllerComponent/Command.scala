package controller.controllerComponent

import scala.util.Try

trait Command:
  def doStep(): Try[Unit]
  def undoStep(): Try[Unit]
