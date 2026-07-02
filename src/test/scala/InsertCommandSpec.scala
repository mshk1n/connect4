package controller

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import model.{Board, BoardInterface}
import scala.util.{Success, Failure, Try}

class InsertCommandSpec extends AnyWordSpec with Matchers {

  class StubBoard extends Board {
    var removedRow: Int = -1
    var removedCol: Int = -1

    override def removeChip(row: Int, col: Int): Unit = {
      removedRow = row
      removedCol = col
    }
  }

  class MockGameController(stubBoard: BoardInterface) extends GameController(stubBoard, null) {
    var makeMoveResult: Try[Unit] = Success(())
    var undoStateCalled: GameState = null
    var mockCurrentState: GameState = null
    var mockPlacedRow: Option[Int] = Some(5)

    override def getCurrentState: GameState = mockCurrentState

    override def getPlacedRowOfColumn(col: Int): Option[Int] = mockPlacedRow

    override def undoGameStatus(previousState: GameState): Unit = {
      undoStateCalled = previousState
    }
  }

  class MockGameState(gc: MockGameController) extends GameState {
    override def currentPlayer: Option[model.Player] = None
    override def makeMove(col: Int): Try[Unit] = gc.makeMoveResult
  }

  "An InsertCommand" should {

    "successfully execute doStep and record placed row" in {
      val board = new StubBoard
      val gc = new MockGameController(board)
      val state = new MockGameState(gc)
      gc.mockCurrentState = state
      gc.makeMoveResult = Success(())
      gc.mockPlacedRow = Some(5)

      val command = new InsertCommand(gc, 3)
      val result = command.doStep()

      result should be(Success(()))
    }

    "return Failure on doStep if internal state execution fails" in {
      val board = new StubBoard
      val gc = new MockGameController(board)
      val state = new MockGameState(gc)
      gc.mockCurrentState = state
      val error = new RuntimeException("Execution error")
      gc.makeMoveResult = Failure(error)

      val command = new InsertCommand(gc, 3)
      val result = command.doStep()

      result should be(Failure(error))
    }

    "successfully execute undoStep and revert board chip along with previous state" in {
      val board = new StubBoard
      val gc = new MockGameController(board)
      val state = new MockGameState(gc)
      gc.mockCurrentState = state
      gc.makeMoveResult = Success(())
      gc.mockPlacedRow = Some(4)

      val command = new InsertCommand(gc, 2)
      command.doStep() should be(Success(()))

      val undoResult = command.undoStep()
      undoResult should be(Success(()))
      board.removedRow should be(4)
      board.removedCol should be(2)
      gc.undoStateCalled should be(state)
    }

    "return Failure on undoStep when there is no recorded move history" in {
      val board = new StubBoard
      val gc = new MockGameController(board)
      val state = new MockGameState(gc)
      gc.mockCurrentState = state

      val command = new InsertCommand(gc, 2)
      val undoResult = command.undoStep()

      undoResult.isFailure should be(true)
      undoResult.failed.get shouldBe a[IllegalStateException]
      undoResult.failed.get.getMessage should include("No move to undo!")
    }

    "successfully execute redoStep by caching current state and running doStep" in {
      val board = new StubBoard
      val gc = new MockGameController(board)
      val state1 = new MockGameState(gc)
      val state2 = new MockGameState(gc)
      
      gc.mockCurrentState = state1
      val command = new InsertCommand(gc, 1)

      gc.mockCurrentState = state2
      gc.makeMoveResult = Success(())
      gc.mockPlacedRow = Some(5)

      val redoResult = command.redoStep()
      redoResult should be(Success(()))
    }
  }
}