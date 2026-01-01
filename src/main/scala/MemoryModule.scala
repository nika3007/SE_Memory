import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule

import controller.controllerComponent.ControllerAPI
import controller.controllerComponent.controllerBaseImpl.ControllerImpl
import model.modelComponent.MemoryGameAPI

class MemoryModule(game: MemoryGameAPI)
  extends AbstractModule with ScalaModule {

  override def configure(): Unit = {
    // Game wird von außen gebaut (Theme, AI, Levels wählbar!)
    bind[MemoryGameAPI].toInstance(game)

    // Controller bekommt exakt dieses Game
    bind[ControllerAPI].toInstance(new ControllerImpl(game))
  }
}
