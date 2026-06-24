package controller

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import model.Board
import scala.util.{Success, Failure}
import fileio.FileIOInterface

class GameControllerSpec extends AnyWordSpec with Matchers {

  "A GameController" should {

    "properly setup players" in {
      val board = model.BoardFactory.createBoard()
      val fileIO: FileIOInterface = null.asInstanceOf[FileIOInterface]
      val gc = controller.GameControllerFactory.createControlller(board, fileIO)
      gc.setupPlayers(("Alice", "X"), ("Bob", "O"))
      gc.getPlayer.name shouldBe "Alice"
    }

    "manage current player index correctly on successful moves" in {
      val board = model.BoardFactory.createBoard()
      val fileIO: FileIOInterface = null.asInstanceOf[FileIOInterface]
      val gc = controller.GameControllerFactory.createControlller(board, fileIO)
      gc.setupPlayers(("Alice", "X"), ("Bob", "O"))
      
      gc.makeMove(0) shouldBe Success(())
      gc.getPlayer.name shouldBe "Bob"
      
      gc.makeMove(0) shouldBe Success(())
      gc.getPlayer.name shouldBe "Alice"
    }

    "not switch player and return Failure if move was invalid (column full)" in {
      val board = model.BoardFactory.createBoard()
      val fileIO: FileIOInterface = null.asInstanceOf[FileIOInterface]
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
      val fileIO: FileIOInterface = null.asInstanceOf[FileIOInterface]
      val gc = controller.GameControllerFactory.createControlller(board, fileIO)
      gc.setupPlayers(("Alice", "X"), ("Bob", "O"))

      gc.makeMove(0) // A
      gc.makeMove(0) // B
      gc.makeMove(1) // A
      gc.makeMove(1) // B
      gc.makeMove(2) // A
      gc.makeMove(2) // B
      gc.makeMove(3) // A

      gc.isGameOver shouldBe true
      
      gc.makeMove(4) shouldBe a[Failure[?]]
    }
  }
}