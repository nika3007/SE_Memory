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
    // wegen Konstruktor Parameter toInstance, deshaln kein inject in memoryimpl
    val game = new MemoryGameImpl(theme, ai, levels)
    bind[MemoryGameAPI].toInstance(game) 
    //bind[MemoryGameAPI].toInstance(new MemoryGameImpl(theme, ai, levels)) ---> vereinfacht
    
    // Controller bekommt exakt dieses Game
    bind[ControllerAPI].to[ControllerImpl] //brauchen im controller inject, MemoryApi kommt aus injector, guice erstellt

