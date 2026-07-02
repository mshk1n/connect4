package model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class BoardSpec extends AnyWordSpec with Matchers {
  "A Board" should {
    "be initialized with empty cells" in {
      val board = new Board()
      board.getCell(0, 0) should be(" ")
    }

    "render a string representation" in {
      val board = new Board()
      val rendered = board.render()
      rendered should include("|   |")
      rendered should include("---")
    }

    "return None for out-of-bounds columns when dropping a chip" in {
      val board = new Board()
      board.dropChip(-1, "X") should be(None)
      board.dropChip(7, "X") should be(None)
    }

    "correctly find the lowest empty row" in {
      val board = new Board()
      board.dropChip(0, "X") should be(Some((5, 0)))
      board.getCell(5, 0) should be("X")
      board.dropChip(0, "O") should be(Some((4, 0)))
      board.getCell(4, 0) should be("O")
    }

    "return None if column is completely full" in {
      val board = new Board()
      for (_ <- 0 until 6) board.dropChip(0, "X")
      board.dropChip(0, "Y") should be(None)
    }

    "correctly report if the board is full or not" in {
      val board = new Board()
      board.isFull should be(false)

      for (col <- 0 until board.cols) {
        for (_ <- 0 until board.rows) {
          board.dropChip(col, "X")
        }
      }
      board.isFull should be(true)
    }

    "allow setting and removing cells within valid bounds" in {
      val board = new Board()
      board.setCell(2, 3, "O")
      board.getCell(2, 3) should be("O")

      board.removeChip(2, 3)
      board.getCell(2, 3) should be(" ")
    }

    "ignore setCell and removeChip operations when coordinates are out of bounds" in {
      val board = new Board()
      
      board.setCell(-1, 0, "X")
      board.setCell(6, 0, "X")
      board.setCell(0, -1, "X")
      board.setCell(0, 7, "X")

      board.removeChip(-1, 0)
      board.removeChip(6, 0)
      board.removeChip(0, -1)
      board.removeChip(0, 7)
      
      board.isFull should be(false)
    }

    "throw an IndexOutOfBoundsException when calling getCell with invalid coordinates" in {
      val board = new Board()
      assertThrows[IndexOutOfBoundsException] {
        board.getCell(-1, 0)
      }
      assertThrows[IndexOutOfBoundsException] {
        board.getCell(0, 7)
      }
    }

    "be created successfully using BoardFactory" in {
      val board = BoardFactory.createBoard()
      board.rows should be(6)
      board.cols should be(7)
    }
  }
}