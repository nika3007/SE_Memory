package model.modelComponent

import model.*
import model.boardComponent.BoardAPI

trait MemoryGameAPI:
  def theme: Theme
  def ai: AIPlayer
  def levels: Vector[Level]

  def currentLevel: Level
  def levelsCount: Int
  def currentLevelIndex: Int
  def currentLevelNumber: Int

  def board: BoardAPI
  //def board_=(b: Board): Unit

  def save(): GameMemento
  def restore(m: GameMemento): Unit

  def nextLevel(): Boolean

  def setTheme(name: String): Unit
  def setAI(name: String): Unit

