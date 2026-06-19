package util

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import model.Board

class ConnectNStrategySpec extends AnyWordSpec with Matchers {

  class MockBoard extends Board {
    val matrix: Array[Array[String]] = Array.fill(6, 7)(" ")
    
    val height: Int = 6
    val width: Int = 7
    override def getCell(row: Int, col: Int): String = matrix(row)(col)
    override def dropChip(col: Int, symbol: String): Option[(Int, Int)] = None
    override def removeChip(row: Int, col: Int): Unit = {}
    override def isFull: Boolean = false
    override def render(): String = ""
  }

  "A ConnectNStrategy" should {

    "return false if the cell is empty" in {
      val board = new MockBoard
      val strategy = new ConnectNStrategy(4)
      
      strategy.checkWin(board, 3, 3) should be(false)
    }

    "correctly detect a horizontal win" in {
      val board = new MockBoard
      val strategy = new ConnectNStrategy(4)
      
      board.matrix(3)(1) = "X"
      board.matrix(3)(2) = "X"
      board.matrix(3)(3) = "X"
      board.matrix(3)(4) = "X"

      strategy.checkWin(board, 3, 3) should be(true)
    }

    "correctly detect a vertical win" in {
      val board = new MockBoard
      val strategy = new ConnectNStrategy(4)
      
      board.matrix(2)(2) = "O"
      board.matrix(3)(2) = "O"
      board.matrix(4)(2) = "O"
      board.matrix(5)(2) = "O"

      strategy.checkWin(board, 4, 2) should be(true)
    }

    "correctly detect a diagonal win (top-left to bottom-right)" in {
      val board = new MockBoard
      val strategy = new ConnectNStrategy(4)
      
      board.matrix(1)(1) = "X"
      board.matrix(2)(2) = "X"
      board.matrix(3)(3) = "X"
      board.matrix(4)(4) = "X"

      strategy.checkWin(board, 3, 3) should be(true)
    }

    "correctly detect a diagonal win (top-right to bottom-left)" in {
      val board = new MockBoard
      val strategy = new ConnectNStrategy(4)
      
      board.matrix(1)(4) = "O"
      board.matrix(2)(3) = "O"
      board.matrix(3)(2) = "O"
      board.matrix(4)(1) = "O"

      strategy.checkWin(board, 2, 3) should be(true)
    }

    "not detect a win if there are not enough chips in a row" in {
      val board = new MockBoard
      val strategy = new ConnectNStrategy(4)
      
      board.matrix(5)(0) = "X"
      board.matrix(5)(1) = "X"
      board.matrix(5)(2) = "X"

      strategy.checkWin(board, 5, 1) should be(false)
    }

    "work correctly with dynamic win conditions (e.g. Connect 3 or Connect 5)" in {
      val board = new MockBoard
      val strategy3 = new ConnectNStrategy(3)
      val strategy5 = new ConnectNStrategy(5)

      board.matrix(0)(0) = "X"
      board.matrix(0)(1) = "X"
      board.matrix(0)(2) = "X"

      strategy3.checkWin(board, 0, 1) should be(true)
      strategy5.checkWin(board, 0, 1) should be(false)
    }
  }
}