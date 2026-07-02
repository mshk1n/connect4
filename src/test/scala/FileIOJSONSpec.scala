package fileio

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import model.{BoardFactory, Player}
import controller.{PlayerTurnState, InitializationState}
import scala.util.{Success, Failure}
import java.io.File

class FileIOJSONSpec extends AnyWordSpec with Matchers {

  "A FileIOJSON" should {

    "save and load a game state successfully with PlayerTurnState" in {
      val fileIO = new FileIOJSON()
      val board = BoardFactory.createBoard()
      board.setCell(1, 1, "X")
      board.setCell(4, 5, "O")
      
      val players = List(Player("Alice", "X"), Player("Bob", "O"))
      val state = new PlayerTurnState(null, 1)
      
      val saveResult = fileIO.save(board, players, state, 4)
      saveResult.isSuccess should be(true)

      val loadResult = fileIO.load
      loadResult.isSuccess should be(true)

      val (loadedBoard, loadedPlayers, loadedState, loadedWinCount) = loadResult.get
      loadedWinCount should be(4)
      loadedPlayers should be(players)
      loadedBoard.getCell(1, 1) should be("X")
      loadedBoard.getCell(4, 5) should be("O")
      
      loadedState shouldBe a[PlayerTurnState]
      loadedState.asInstanceOf[PlayerTurnState].playerIndex should be(1)
    }

    "fallback to player index 0 when saving a non-PlayerTurnState" in {
      val fileIO = new FileIOJSON()
      val board = BoardFactory.createBoard()
      val players = List(Player("Alice", "X"), Player("Bob", "O"))
      val mockState = new InitializationState(null)

      val saveResult = fileIO.save(board, players, mockState, 5)
      saveResult.isSuccess should be(true)

      val loadResult = fileIO.load
      loadResult.isSuccess should be(true)

      val (_, _, loadedState, loadedWinCount) = loadResult.get
      loadedWinCount should be(5)
      loadedState.asInstanceOf[PlayerTurnState].playerIndex should be(0)
    }

    "return a Failure when loading a non-existent file" in {
      val fileIO = new FileIOJSON()
      val file = new File("game.json")
      if (file.exists()) file.delete()

      val loadResult = fileIO.load
      loadResult.isFailure should be(true)
    }
  }
}