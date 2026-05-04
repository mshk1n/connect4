package m4connect

import model.Board
import controller.GameController
import view.TUI
import scala.io.StdIn.readLine

@main def run(): Unit =
  //component initialisation
  val board = new Board()
  val controller = new GameController(board)
  val tui = new TUI(controller)

  //creating players
  val p1 = tui.registerPlayer(1)
  val p2 = tui.registerPlayer(2)
  controller.setupPlayers(p1, p2)

  println("\n--- Game Started! ---")
  println(controller.boardToString)

  while true do
    val player = controller.getPlayer
    print(s"\n${player.name}'s turn (${player.symbol}) > ")
    
    val input = readLine()
    tui.processInput(input)