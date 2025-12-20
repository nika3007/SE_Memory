package model

import model.modelComponent.MemoryGameAPI
import model.modelComponent.implModel.MemoryGameImpl

object MemoryGame:
  def apply(theme: Theme, ai: AIPlayer, levels: Vector[Level]): MemoryGameAPI =
    new MemoryGameImpl(theme, ai, levels)
