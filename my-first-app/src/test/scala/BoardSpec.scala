import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class BoardSpec extends AnyWordSpec with Matchers {

  "A Board" when {
    "created" should {
      val board = Board()

      "have 6 rows and 7 columns" in {
        board.rows shouldBe 6
        board.cols shouldBe 7
      }

      "be empty at the start" in {
        for (r <- 0 until board.rows; c <- 0 until board.cols) {
          board.getCell(r, c) shouldBe " "
        }
      }

      "return a string representation" in {
        val output = board.printBoard()
        output shouldBe a [String]
        output should not be empty
      }

      "let Player drop chips" in {
        board.dropChip(2, "O")
        board.getCell(5, 2) shouldBe "O"
      }

      "stack chips on each other inside a single column" in {
        val testBoard = Board()

        testBoard.dropChip(2, "O")
        testBoard.dropChip(2, "X")
        testBoard.getCell(5, 2) shouldBe "O"
        testBoard.getCell(4, 2) shouldBe "X"
      }
    }
  }
}