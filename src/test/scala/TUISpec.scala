package view

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import controller.GameController
import model.Board
import java.io.ByteArrayOutputStream
import fileio.FileIOInterface

class TUISpec extends AnyWordSpec with Matchers {
  "A TUI" should {
    val board = model.BoardFactory.createBoard()
    val fileIO: FileIOInterface = null.asInstanceOf[FileIOInterface]
    val gc = controller.GameControllerFactory.createControlller(board, fileIO)
    gc.setupPlayers(("P1", "X"), ("P2", "O"))
    val tui = new TUI(gc)

    "update when the controller changes" in {
      val out = new ByteArrayOutputStream()
      Console.withOut(out) {
        tui.update()
      }
      out.toString should include("---")
    }

    "handle valid input" in {
      gc.makeMove(0)
      tui.processInput("1") 
      board.getCell(5, 1) should not be (" ")
    }

    "handle invalid column number" in {
      val out = new ByteArrayOutputStream()
      Console.withOut(out) {
        tui.processInput("10")
      }
      out.toString should include("Error")
    }

    "handle non-integer input" in {
      val out = new ByteArrayOutputStream()
      Console.withOut(out) {
        tui.processInput("abc")
      }
      out.toString should include("Please type a number")
    }
  }
}