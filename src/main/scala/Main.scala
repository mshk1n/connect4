package m4connect

import model.Board
import controller.GameController
import view._
import scala.io.StdIn.readLine
import scala.annotation.tailrec

@main def run(): Unit =
  val board = new Board()
  val controller = new GameController(board)
  val tui = new TUI(controller)
  scala.swing.Swing.onEDT {
    val gui = new MainGUI(controller)
    gui.visible = true
  }