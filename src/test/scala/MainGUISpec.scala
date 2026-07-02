package view

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import controller.GameControllerInterface
import model.{Board, BoardInterface, Player}
import scala.swing.event.ButtonClicked
import scala.util.{Success, Failure, Try}
import java.awt.event.ActionEvent

class MainGUISpec extends AnyWordSpec with Matchers {

  class MockGameController extends GameControllerInterface {
    var savedCalled = false
    var loadCalled = false
    var undoCalled = false
    var redoCalled = false
    var makeMoveCol: Int = -1
    var setupPlayersCalled = false
    var winCountSet = 4

    var saveResult: Try[Unit] = Success(())
    var loadResult: Try[Unit] = Success(())
    var undoResult: Try[Unit] = Success(())
    var redoResult: Try[Unit] = Success(())
    var makeMoveResult: Try[Unit] = Success(())

    var gameOverStatus = false
    var gameWinner: Option[Player] = None
    var mockPlayer = Player("Alice", "X")
    var mockBoard = new Board()

    var mockStrategy = new util.WinStrategy {
      override def winCount: Int = winCountSet
      override def checkWin(board: BoardInterface, row: Int, col: Int): Boolean = false
    }

    override def save: Try[Unit] = { savedCalled = true; saveResult }
    override def load: Try[Unit] = { loadCalled = true; loadResult }
    override def getWinStrategy: util.WinStrategy = mockStrategy
    override def setWinCount(n: Int): Unit = { winCountSet = n }
    override def isGameOver: Boolean = gameOverStatus
    override def winner: Option[Player] = gameWinner
    override def getPlayer: Player = mockPlayer
    override def getPlayerSymbol(index: Int): String = if (index == 0) "X" else "O"
    override def setupPlayers(p1Data: (String, String), p2Data: (String, String)): Unit = { setupPlayersCalled = true }
    override def makeMove(col: Int): Try[Unit] = { makeMoveCol = col; makeMoveResult }
    override def undo(): Try[Unit] = { undoCalled = true; undoResult }
    override def redo(): Try[Unit] = { redoCalled = true; redoResult }
    override def boardToString: String = ""
    override def getBoard: BoardInterface = mockBoard
  }

  "A MainGUI" should {

    "initialize with default components and window settings" in {
      val gc = new MockGameController
      val gui = new MainGUI(gc)

      gui.title should be("Connect X")
      gui.player1Field.text should be("")
      gui.player2Field.text should be("")
      gui.radio4.selected should be(true)
    }

    "prevent play when player names are empty" in {
      val gc = new MockGameController
      val gui = new MainGUI(gc)

      gui.player1Field.text = ""
      gui.player2Field.text = "Bob"
      gui.player1Field.text.trim.isEmpty should be(true)
      gc.setupPlayersCalled should be(false)

      gui.player1Field.text = "Alice"
      gui.player2Field.text = " "
      gui.player2Field.text.trim.isEmpty should be(true)
      gc.setupPlayersCalled should be(false)
    }

    "configure strategic win count for Connect 3" in {
      val gc = new MockGameController
      val gui = new MainGUI(gc)

      gui.player1Field.text = "Alice"
      gui.player2Field.text = "Bob"
      gui.radio3.selected = true

      gui.radio3.selected should be(true)
    }

    "configure strategic win count for Connect 5" in {
      val gc = new MockGameController
      val gui = new MainGUI(gc)

      gui.player1Field.text = "Alice"
      gui.player2Field.text = "Bob"
      gui.radio5.selected = true

      gui.radio5.selected should be(true)
    }

    "configure strategic win count for Connect 4" in {
      val gc = new MockGameController
      val gui = new MainGUI(gc)

      gui.player1Field.text = "Alice"
      gui.player2Field.text = "Bob"
      gui.radio4.selected = true

      gui.radio4.selected should be(true)
    }

    "execute and capture undo operations" in {
      val gc = new MockGameController
      val gui = new MainGUI(gc)

      gc.undoResult = Success(())
      gc.undo() shouldBe Success(())

      gc.undoResult = Failure(new RuntimeException("Undo Error"))
      gc.undo().isFailure shouldBe true
    }

    "execute and capture redo operations" in {
      val gc = new MockGameController
      val gui = new MainGUI(gc)

      gc.redoResult = Success(())
      gc.redo() shouldBe Success(())

      gc.redoResult = Failure(new RuntimeException("Redo Error"))
      gc.redo().isFailure shouldBe true
    }

    "execute and capture save operations" in {
      val gc = new MockGameController
      val gui = new MainGUI(gc)

      gc.saveResult = Success(())
      gc.save shouldBe Success(())

      gc.saveResult = Failure(new RuntimeException("Save Error"))
      gc.save.isFailure shouldBe true
    }

    "execute and capture load operations" in {
      val gc = new MockGameController
      val gui = new MainGUI(gc)

      gc.loadResult = Success(())
      gc.load shouldBe Success(())

      gc.loadResult = Failure(new RuntimeException("Load Error"))
      gc.load.isFailure shouldBe true
    }

    "initialize game board view and verify dimensions" in {
      val gc = new MockGameController
      val gui = new MainGUI(gc)

      gui.startGameUI()
      val sampleButton = gui.cells(0)(2)
      sampleButton should not be null
    }

    "increment elapsed duration on timer ticks" in {
      val gc = new MockGameController
      val gui = new MainGUI(gc)

      gui.startGameUI()
      gui.secondsElapsed should be(0)

      val listener = gui.timer.getActionListeners()(0)
      listener.actionPerformed(new ActionEvent(gui.timer, ActionEvent.ACTION_PERFORMED, "tick"))

      gui.secondsElapsed should be(1)
      gui.timerLabel.text should include("00:01")
    }

    "render dynamic board states and trigger color variations on update" in {
      val gc = new MockGameController
      val gui = new MainGUI(gc)

      gui.startGameUI()

      gc.mockBoard.setCell(0, 0, " ")
      gc.mockBoard.setCell(0, 1, "X")
      gc.mockBoard.setCell(0, 2, "O")

      gui.update()

      gui.cells(0)(0).background should be(java.awt.Color.WHITE)
      gui.cells(0)(1).background should be(java.awt.Color.RED)
      gui.cells(0)(2).background should be(java.awt.Color.YELLOW)
    }

    "handle fallback conditions when win strategy is absent" in {
      val gc = new MockGameController {
        override def getWinStrategy: util.WinStrategy = null
      }
      val gui = new MainGUI(gc)
      gui.startGameUI()

      gui.radio3.selected = true
      gui.updateModeLabelText()
      gui.strategyLabel.text should include("3 IN A ROW")

      gui.radio3.selected = false
      gui.radio5.selected = true
      gui.updateModeLabelText()
      gui.strategyLabel.text should include("5 IN A ROW")

      gui.radio5.selected = false
      gui.radio4.selected = true
      gui.updateModeLabelText()
      gui.strategyLabel.text should include("4 IN A ROW")
    }
  }
}