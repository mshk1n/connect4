package view

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import controller.GameControllerInterface
import model.{BoardInterface, Board, Player}
import scala.util.{Try, Success, Failure}
import java.io.{ByteArrayOutputStream, StringReader}

class TUISpec extends AnyWordSpec with Matchers {

  class MockGameController extends GameControllerInterface {
    var undoResult: Try[Unit] = Success(())
    var redoResult: Try[Unit] = Success(())
    var makeMoveResult: Try[Unit] = Success(())
    var gameOverStatus = false
    var gameWinner: Option[Player] = None
    var boardStringOutput = "mock_board"

    override def save: Try[Unit] = Success(())
    override def load: Try[Unit] = Success(())
    override def getWinStrategy: util.WinStrategy = null
    override def setWinCount(n: Int): Unit = {}
    override def isGameOver: Boolean = gameOverStatus
    override def winner: Option[Player] = gameWinner
    override def getPlayer: Player = Player("Mock", "M")
    override def getPlayerSymbol(index: Int): String = ""
    override def setupPlayers(p1Data: (String, String), p2Data: (String, String)): Unit = {}
    override def makeMove(col: Int): Try[Unit] = makeMoveResult
    override def undo(): Try[Unit] = undoResult
    override def redo(): Try[Unit] = redoResult
    override def boardToString: String = boardStringOutput
    override def getBoard: BoardInterface = new Board()
  }

  "A TUI" should {

    "print the board on update when game is active" in {
      val gc = new MockGameController
      val tui = new TUI(gc)
      val out = new ByteArrayOutputStream()

      Console.withOut(out) {
        tui.update()
      }
      out.toString should include("mock_board")
      out.toString should not include("Congratulations")
    }

    "print winner details on update when game is over with a winner" in {
      val gc = new MockGameController
      gc.gameOverStatus = true
      gc.gameWinner = Some(Player("Alice", "X"))
      val tui = new TUI(gc)
      val out = new ByteArrayOutputStream()

      Console.withOut(out) {
        tui.update()
      }
      out.toString should include("Congratulations! Alice won!")
      out.toString should include("Exiting...")
    }

    "print draw details on update when game is over without a winner" in {
      val gc = new MockGameController
      gc.gameOverStatus = true
      gc.gameWinner = None
      val tui = new TUI(gc)
      val out = new ByteArrayOutputStream()

      Console.withOut(out) {
        tui.update()
      }
      out.toString should include("It's a draw! Game over.")
      out.toString should include("Exiting...")
    }

    "handle successful undo commands" in {
      val gc = new MockGameController
      gc.undoResult = Success(())
      val tui = new TUI(gc)
      val out = new ByteArrayOutputStream()

      Console.withOut(out) {
        tui.processInput("z")
      }
      out.toString should include("Undo successful.")
    }

    "handle failed undo commands" in {
      val gc = new MockGameController
      gc.undoResult = Failure(new RuntimeException("No steps left"))
      val tui = new TUI(gc)
      val out = new ByteArrayOutputStream()

      Console.withOut(out) {
        tui.processInput("Z")
      }
      out.toString should include("Cannot undo: No steps left")
    }

    "handle successful redo commands" in {
      val gc = new MockGameController
      gc.redoResult = Success(())
      val tui = new TUI(gc)
      val out = new ByteArrayOutputStream()

      Console.withOut(out) {
        tui.processInput("y")
      }
      out.toString should include("Redo successful.")
    }

    "handle failed redo commands" in {
      val gc = new MockGameController
      gc.redoResult = Failure(new RuntimeException("No steps ahead"))
      val tui = new TUI(gc)
      val out = new ByteArrayOutputStream()

      Console.withOut(out) {
        tui.processInput("Y")
      }
      out.toString should include("Cannot redo: No steps ahead")
    }

    "handle successful move inputs" in {
      val gc = new MockGameController
      gc.makeMoveResult = Success(())
      val tui = new TUI(gc)
      val out = new ByteArrayOutputStream()

      Console.withOut(out) {
        tui.processInput("3")
      }
      out.toString should be("")
    }

    "handle failed move inputs" in {
      val gc = new MockGameController
      gc.makeMoveResult = Failure(new RuntimeException("Column full"))
      val tui = new TUI(gc)
      val out = new ByteArrayOutputStream()

      Console.withOut(out) {
        tui.processInput(" 3 ")
      }
      out.toString should include("Error! Column full")
    }

    "handle non-integer inputs gracefully" in {
      val gc = new MockGameController
      val tui = new TUI(gc)
      val out = new ByteArrayOutputStream()

      Console.withOut(out) {
        tui.processInput("abc")
      }
      out.toString should include("Error! Please type a number")
    }

    "register player 0 with symbol X" in {
      val gc = new MockGameController
      val tui = new TUI(gc)
      val in = new StringReader("Alice\n")
      val out = new ByteArrayOutputStream()

      val result = Console.withIn(in) {
        Console.withOut(out) {
          tui.registerPlayer(0)
        }
      }
      result should be(("Alice", "X"))
      out.toString should include("Register Player 0")
    }

    "register player 1 with symbol O" in {
      val gc = new MockGameController
      val tui = new TUI(gc)
      val in = new StringReader("Bob\n")
      val out = new ByteArrayOutputStream()

      val result = Console.withIn(in) {
        Console.withOut(out) {
          tui.registerPlayer(1)
        }
      }
      result should be(("Bob", "O"))
      out.toString should include("Register Player 1")
    }
  }
}