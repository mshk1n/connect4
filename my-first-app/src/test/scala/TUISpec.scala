package view

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import controller.GameController
import model.Board
import java.io.ByteArrayOutputStream

class TUISpec extends AnyWordSpec with Matchers {
  "A TUI" should {
    val board = new Board()
    val controller = new GameController(board)
    controller.setupPlayers(("P1", "X"), ("P2", "O"))
    val tui = new TUI(controller)

    "update when the controller changes" in {
      val out = new ByteArrayOutputStream()
      Console.withOut(out) {
        tui.update()
      }
      out.toString should include("---")
    }

    "handle valid input" in {
      controller.makeMove(0)
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