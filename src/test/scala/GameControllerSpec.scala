package controller

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import model.{Board, BoardFactory, Player}
import controller.{PlayerTurnState, InitializationState, GameOverState}
import scala.util.{Success, Failure, Try}
import fileio.FileIOInterface

class GameControllerSpec extends AnyWordSpec with Matchers {

  class MockFileIO extends FileIOInterface {
    var saved = false
    var loadResult: Try[(model.BoardInterface, List[Player], controller.GameState, Int)] = 
      Failure(new Exception("Default load failure"))

    override def save(board: model.BoardInterface, players: List[Player], currentState: controller.GameState, winCount: Int): Try[Unit] = {
      saved = true
      Success(())
    }

    override def load: Try[(model.BoardInterface, List[Player], controller.GameState, Int)] = loadResult
  }

  "A GameController" should {

    "properly setup players" in {
      val board = model.BoardFactory.createBoard()
      val fileIO = new MockFileIO
      val gc = controller.GameControllerFactory.createControlller(board, fileIO).asInstanceOf[GameController]
      gc.setupPlayers(("Alice", "X"), ("Bob", "O"))
      gc.getPlayer.name shouldBe "Alice"
      gc.getPlayerSymbol(0) shouldBe "X"
      gc.getPlayerSymbol(1) shouldBe "O"
    }

    "manage current player index correctly on successful moves" in {
      val board = model.BoardFactory.createBoard()
      val fileIO = new MockFileIO
      val gc = controller.GameControllerFactory.createControlller(board, fileIO)
      gc.setupPlayers(("Alice", "X"), ("Bob", "O"))
      
      gc.makeMove(0) shouldBe Success(())
      gc.getPlayer.name shouldBe "Bob"
      
      gc.makeMove(0) shouldBe Success(())
      gc.getPlayer.name shouldBe "Alice"
    }

    "not switch player and return Failure if move was invalid (column full)" in {
      val board = model.BoardFactory.createBoard()
      val fileIO = new MockFileIO
      val gc = controller.GameControllerFactory.createControlller(board, fileIO)
      gc.setupPlayers(("Alice", "X"), ("Bob", "O"))

      for (_ <- 0 until 6) {
        gc.makeMove(0) shouldBe Success(())
      }

      val currentFeedback = gc.getPlayer
      gc.makeMove(0) shouldBe a[Failure[?]]
      gc.getPlayer shouldBe currentFeedback
    }

    "return Failure when attempting a move after the game is over" in {
      val board = model.BoardFactory.createBoard()
      val fileIO = new MockFileIO
      val gc = controller.GameControllerFactory.createControlller(board, fileIO)
      gc.setupPlayers(("Alice", "X"), ("Bob", "O"))

      gc.makeMove(0)
      gc.makeMove(0)
      gc.makeMove(1)
      gc.makeMove(1)
      gc.makeMove(2)
      gc.makeMove(2)
      gc.makeMove(3)

      gc.isGameOver shouldBe true
      gc.winner.isDefined shouldBe true
      
      gc.makeMove(4) shouldBe a[Failure[?]]
    }

    "return default values when uninitialized" in {
      val board = model.BoardFactory.createBoard()
      val fileIO = new MockFileIO
      val gc = new GameController(board, fileIO)

      gc.isGameOver shouldBe false
      gc.winner shouldBe None
      gc.getPlayer shouldBe Player("Unknown", "?")
      gc.getBoard shouldBe board
    }

    "allow configuring the win strategy count" in {
      val board = model.BoardFactory.createBoard()
      val fileIO = new MockFileIO
      val gc = new GameController(board, fileIO)

      gc.setWinCount(5)
      gc.getWinStrategy.winCount shouldBe 5
    }

    "correctly forward internal state mutations" in {
      val board = model.BoardFactory.createBoard()
      val fileIO = new MockFileIO
      val gc = new GameController(board, fileIO)

      val targetState = new InitializationState(gc)
      gc.changeState(targetState)
      gc.getCurrentState shouldBe targetState
    }

    "render the underlying board to string format" in {
      val board = model.BoardFactory.createBoard()
      val fileIO = new MockFileIO
      val gc = new GameController(board, fileIO)

      gc.boardToString shouldBe board.render()
    }

    "locate the highest placed row of a column accurately" in {
      val board = model.BoardFactory.createBoard()
      val fileIO = new MockFileIO
      val gc = new GameController(board, fileIO)
      gc.setupPlayers(("Alice", "X"), ("Bob", "O"))

      gc.getPlacedRowOfColumn(2) shouldBe None
      gc.makeMove(2)
      gc.getPlacedRowOfColumn(2) shouldBe Some(5)
    }

    "execute internal move logic safely" in {
      val board = model.BoardFactory.createBoard()
      val fileIO = new MockFileIO
      val gc = new GameController(board, fileIO)
      gc.setupPlayers(("Alice", "X"), ("Bob", "O"))

      gc.executeMoveLogic(3) shouldBe Some((5, 3))
      gc.executeMoveLogic(-1) shouldBe None
    }

    "allow manually setting game status during undo chains" in {
      val board = model.BoardFactory.createBoard()
      val fileIO = new MockFileIO
      val gc = new GameController(board, fileIO)

      val stateA = new InitializationState(gc)
      val stateB = new PlayerTurnState(gc, 0)

      gc.changeState(stateA)
      gc.undoGameStatus(stateB)
      gc.getCurrentState shouldBe stateB
    }

    "trigger file save correctly via fileIO dependency" in {
      val board = model.BoardFactory.createBoard()
      val fileIO = new MockFileIO
      val gc = new GameController(board, fileIO)

      gc.save shouldBe Success(())
      fileIO.saved shouldBe true
    }

    "handle error results from fileIO load requests" in {
      val board = model.BoardFactory.createBoard()
      val fileIO = new MockFileIO
      val gc = new GameController(board, fileIO)

      val customError = new RuntimeException("Load failed")
      fileIO.loadResult = Failure(customError)

      gc.load shouldBe Failure(customError)
    }

    "re-initialize session on successful load with a PlayerTurnState" in {
      val board = model.BoardFactory.createBoard()
      val fileIO = new MockFileIO
      val gc = new GameController(board, fileIO)

      val loadedBoard = model.BoardFactory.createBoard()
      val loadedPlayers = List(Player("User1", "1"), Player("User2", "2"))
      val loadedState = new PlayerTurnState(gc, 1)

      fileIO.loadResult = Success((loadedBoard, loadedPlayers, loadedState, 4))

      gc.load shouldBe Success(())
      gc.getBoard shouldBe loadedBoard
      gc.getPlayerSymbol(0) shouldBe "1"
      gc.getCurrentState.asInstanceOf[PlayerTurnState].playerIndex shouldBe 1
    }

    "fallback to base index on successful load with non-turn status" in {
      val board = model.BoardFactory.createBoard()
      val fileIO = new MockFileIO
      val gc = new GameController(board, fileIO)

      val loadedBoard = model.BoardFactory.createBoard()
      val loadedPlayers = List(Player("User1", "1"), Player("User2", "2"))
      val loadedState = new InitializationState(gc)

      fileIO.loadResult = Success((loadedBoard, loadedPlayers, loadedState, 4))

      gc.load shouldBe Success(())
      gc.getCurrentState.asInstanceOf[PlayerTurnState].playerIndex shouldBe 0
    }

    "integrate cleanly with structural undo and redo pipes" in {
      val board = model.BoardFactory.createBoard()
      val fileIO = new MockFileIO
      val gc = new GameController(board, fileIO)
      gc.setupPlayers(("Alice", "X"), ("Bob", "O"))

      gc.makeMove(4)
      gc.getBoard.getCell(5, 4) shouldBe "X"

      gc.undo() shouldBe Success(())
      gc.getBoard.getCell(5, 4) shouldBe " "

      gc.redo() shouldBe Success(())
      gc.getBoard.getCell(5, 4) shouldBe "X"
    }

    "return None for winner when game is active in any normal state" in {
      val board = model.BoardFactory.createBoard()
      val fileIO = new MockFileIO
      val gc = new GameController(board, fileIO)
      
      gc.changeState(new InitializationState(gc))
      gc.winner shouldBe None

      gc.changeState(new PlayerTurnState(gc, 0))
      gc.winner shouldBe None
    }
  }
}