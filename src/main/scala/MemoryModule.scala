import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule
import controller.controllerComponent.ControllerAPI
import controller.controllerComponent.controllerBaseImpl.ControllerImpl
import model.modelComponent.MemoryGameAPI
import model.modelComponent.implModel.MemoryGameImpl
import model.*
import model.fileIoComponent.FileIOInterface
//import model.fileIoComponent.fileIoJsonImpl
import model.fileIoComponent.fileIoXmlImpl


class MemoryModule(theme: Theme, ai: AIPlayer, levels: Vector[Level]) 
  extends AbstractModule with ScalaModule {

  override def configure(): Unit =
    // bauen Game von außen (Theme, AI, Levels wählen - dann erst game bauen mit UI)
    bind[MemoryGameAPI].toInstance(new MemoryGameImpl(theme, ai, levels))
     
    // Controller bekommt exakt dieses Game
    bind[ControllerAPI].to[ControllerImpl]

    // File I/O 
    //bind[FileIOInterface].to[fileIoJsonImpl.FileIO]

    bind[FileIOInterface].to[fileIoXmlImpl.FileIO]


}


