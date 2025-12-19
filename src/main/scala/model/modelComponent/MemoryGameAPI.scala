package model.modelComponent
import model.Board
import model.Level


trait MemoryGameAPI:
  def board: Board
  def currentLevel: Level
  def levelsCount: Int
  def currentLevelNumber: Int
  def nextLevel(): Boolean
