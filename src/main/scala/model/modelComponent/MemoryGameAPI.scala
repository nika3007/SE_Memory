package model.modelComponent

import model.*

trait MemoryGameAPI:
  def theme: Theme
  def ai: AIPlayer
  def levels: Vector[Level]

  def currentLevel: Level
  def levelsCount: Int
  def currentLevelIndex: Int
  def currentLevelNumber: Int

  def board: Board
  def board_=(b: Board): Unit

  def save(): GameMemento
  def restore(m: GameMemento): Unit

  def nextLevel(): Boolean
