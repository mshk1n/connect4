package mXconnect

import view._
import controller.GameControllerInterface
import com.google.inject.Guice

@main def run(): Unit =
  val injector = Guice.createInjector(new ConnectXModule)
  val gc = injector.getInstance(classOf[GameControllerInterface])
  val tui = new TUI(gc)
  scala.swing.Swing.onEDT {
    val gui = new MainGUI(gc)
    gui.visible = true
  }