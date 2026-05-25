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

    "return None for out-of-bounds columns" in {
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

    "return false if column is completely full" in {
      val board = new Board()
      for (_ <- 0 until 6) board.dropChip(0, "X")
      board.dropChip(0, "Y") should be(None)
    }
  }
}