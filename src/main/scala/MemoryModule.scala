import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule
import controller.controllerComponent.ControllerAPI
import controller.controllerComponent.controllerBaseImpl.ControllerImpl
import model.modelComponent.MemoryGameAPI
import model.modelComponent.implModel.MemoryGameImpl
import model.*


class MemoryModule(theme: Theme, ai: AIPlayer, levels: Vector[Level]) 
  extends AbstractModule with ScalaModule:

  override def configure(): Unit =
    // Game wird von außen gebaut (Theme, AI, Levels wählbar!)
    bind[MemoryGameAPI].toInstance(new MemoryGameImpl(theme, ai, levels))

    // Controller bekommt exakt dieses Game
    bind[ControllerAPI].to[ControllerImpl]

