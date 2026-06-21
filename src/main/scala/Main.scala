package m4connect

import model.Board
import controller.GameController
import view._
import scala.io.StdIn.readLine
import scala.annotation.tailrec

@main def run(): Unit =
  val board = model.BoardFactory.createBoard()
  val gc = controller.GameControllerFactory.createControlller(board)
  val tui = new TUI(gc)
  scala.swing.Swing.onEDT {
    val gui = new MainGUI(gc)
    gui.visible = true
  }