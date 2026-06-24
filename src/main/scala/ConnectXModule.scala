package mXconnect

import com.google.inject.AbstractModule
import controller.{GameControllerInterface, GameController}
import model.{BoardInterface, Board}
import fileio._

class ConnectXModule extends AbstractModule {
    override def configure(): Unit = {
        bind(classOf[BoardInterface]).to(classOf[Board])
        bind(classOf[GameControllerInterface]).to(classOf[GameController])
        bind(classOf[FileIOInterface]).to(classOf[FileIOXML])
        //bind(classOf[FileIOInterface]).to(classOf[FileIOJSON])
    }
}