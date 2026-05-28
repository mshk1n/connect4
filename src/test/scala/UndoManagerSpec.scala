package controller

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import model.Board
import scala.util.Success

class UndoManagerSpec extends AnyWordSpec with Matchers {

  "A GameController Undo/Redo mechanism" should {

    "return None when trying to undo or redo with an empty history" in {
      val board = new Board()
      val gc = new GameController(board)
      gc.setupPlayers(("Alice", "X"), ("Bob", "O"))

      gc.undo() shouldBe None
      gc.redo() shouldBe None
    }

    "successfully undo a made move" in {
      val board = new Board()
      val gc = new GameController(board)
      gc.setupPlayers(("Alice", "X"), ("Bob", "O"))

      val emptyCell = board.getCell(5, 3)

      gc.makeMove(3) shouldBe Success(())
      board.getCell(5, 3) shouldNot be (emptyCell)
      gc.getPlayer.name shouldBe "Bob"

      gc.undo() shouldBe Some(())
      
      board.getCell(5, 3) shouldBe emptyCell 
      gc.getPlayer.name shouldBe "Alice"
    }

    "successfully redo an undone move" in {
      val board = new Board()
      val gc = new GameController(board)
      gc.setupPlayers(("Alice", "X"), ("Bob", "O"))

      val emptyCell = board.getCell(5, 3)

      gc.makeMove(3) // Ход Элис
      val filledCell = board.getCell(5, 3)
      
      gc.undo()

      gc.redo() shouldBe Some(Success(()))
      
      board.getCell(5, 3) shouldBe filledCell
      board.getCell(5, 3) shouldNot be (emptyCell)
      gc.getPlayer.name shouldBe "Bob"
    }

    "clear redo stack when a new move is made after undo" in {
      val board = new Board()
      val gc = new GameController(board)
      gc.setupPlayers(("Alice", "X"), ("Bob", "O"))

      gc.makeMove(3)
      gc.undo()
      gc.makeMove(4)
      
      gc.redo() shouldBe None
    }
  }
}