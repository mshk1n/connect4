package controller

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import model.{Board, BoardInterface, Player}
import scala.util.{Success, Failure, Try}

class GameStateSpec extends AnyWordSpec with Matchers {

  class StubBoard extends Board {
    var isFullResult = false
    override def isFull: Boolean = isFullResult
  }

  class MockGameController(stubBoard: BoardInterface) extends GameController(stubBoard, null) {
    var changedState: GameState = null
    var checkWinResult = false
    var dropChipResult: Option[(Int, Int)] = Some((5, 0))

    override def changeState(state: GameState): Unit = {
      changedState = state
    }

    override def executeMoveLogic(col: Int): Option[(Int, Int)] = dropChipResult

    override val getWinStrategy = new util.WinStrategy {
      override def winCount: Int = 4
      override def checkWin(board: BoardInterface, row: Int, col: Int): Boolean = checkWinResult
    }
  }

  "An InitializationState" should {
    "return Failure when attempting to make a move" in {
      val board = new StubBoard
      val gc = new MockGameController(board)
      val state = new InitializationState(gc)

      val result = state.makeMove(0)
      result.isFailure should be(true)
      result.failed.get.getMessage should include("Game has not started yet")
    }

    "return None for the current player" in {
      val board = new StubBoard
      val gc = new MockGameController(board)
      val state = new InitializationState(gc)

      state.currentPlayer should be(None)
    }
  }

  "A GameOverState" should {
    "return Failure when attempting to make a move" in {
      val board = new StubBoard
      val gc = new MockGameController(board)
      val state = new GameOverState(gc, Some(Player("Alice", "X")))

      val result = state.makeMove(0)
      result.isFailure should be(true)
      result.failed.get.getMessage should include("Game is already over")
    }

    "return None for the current player" in {
      val board = new StubBoard
      val gc = new MockGameController(board)
      val state = new GameOverState(gc, Some(Player("Alice", "X")))

      state.currentPlayer should be(None)
    }
  }

  "A PlayerTurnState" should {
    "return None for currentPlayer if players list is empty" in {
      val board = new StubBoard
      val gc = new MockGameController(board)
      gc.players = Nil
      val state = new PlayerTurnState(gc, 0)

      state.currentPlayer should be(None)
    }

    "return Failure on makeMove if currentPlayer is None" in {
      val board = new StubBoard
      val gc = new MockGameController(board)
      gc.players = Nil
      val state = new PlayerTurnState(gc, 0)

      val result = state.makeMove(0)
      result.isFailure should be(true)
      result.failed.get.getMessage should include("Players are not initialized")
    }

    "return Failure if column selection logic returns None" in {
      val board = new StubBoard
      val gc = new MockGameController(board)
      gc.players = List(Player("Alice", "X"), Player("Bob", "O"))
      gc.dropChipResult = None
      val state = new PlayerTurnState(gc, 0)

      val result = state.makeMove(3)
      result.isFailure should be(true)
      result.failed.get.getMessage should include("Column 3 is full")
    }

    "transition to GameOverState with a winner if checkWin condition matches" in {
      val board = new StubBoard
      val gc = new MockGameController(board)
      gc.players = List(Player("Alice", "X"), Player("Bob", "O"))
      gc.checkWinResult = true
      val state = new PlayerTurnState(gc, 0)

      val result = state.makeMove(0)
      result should be(Success(()))
      gc.changedState shouldBe a[GameOverState]
      gc.changedState.asInstanceOf[GameOverState].winner should be(Some(Player("Alice", "X")))
    }

    "transition to GameOverState as a draw if board is completely full" in {
      val board = new StubBoard
      board.isFullResult = true
      val gc = new MockGameController(board)
      gc.players = List(Player("Alice", "X"), Player("Bob", "O"))
      gc.checkWinResult = false
      val state = new PlayerTurnState(gc, 0)

      val result = state.makeMove(0)
      result should be(Success(()))
      gc.changedState shouldBe a[GameOverState]
      gc.changedState.asInstanceOf[GameOverState].winner should be(None)
    }

    "transition state to the next player under regular game conditions" in {
      val board = new StubBoard
      val gc = new MockGameController(board)
      gc.players = List(Player("Alice", "X"), Player("Bob", "O"))
      gc.checkWinResult = false
      val state = new PlayerTurnState(gc, 0)

      val result = state.makeMove(0)
      result should be(Success(()))
      gc.changedState shouldBe a[PlayerTurnState]
      gc.changedState.asInstanceOf[PlayerTurnState].playerIndex should be(1)
    }
  }
}