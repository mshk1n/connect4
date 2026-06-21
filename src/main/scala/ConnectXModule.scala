package mXconnect

import com.google.inject.AbstractModule
import controller.{GameControllerInterface, GameController}
import model.{BoardInterface, Board}

class ConnectXModule extends AbstractModule {
    override def configure(): Unit = {
        bind(classOf[BoardInterface]).to(classOf[Board])
        bind(classOf[GameControllerInterface]).to(classOf[GameController])
    }
}