package controller

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import model.Board

class GameControllerSpec extends AnyWordSpec with Matchers {
  "A GameController" should {
    val board = new Board()
    val gc = new GameController(board)

    "properly setup players" in {
      gc.setupPlayers(("Alice", "A"), ("Bob", "B"))
      gc.getPlayer.name should be("Alice")
      gc.getPlayer.color should be(util.ConsoleColors.RED)
    }

    "provide access to board rendering" in {
      gc.boardToString should be(board.render())
    }

    "manage current player index correctly" in {
      gc.setupPlayers(("P1", "1"), ("P2", "2"))
      gc.makeMove(0)
      gc.getPlayer.name should be("P2")
      gc.makeMove(0)
      gc.getPlayer.name should be("P1")
    }
    
    "not switch player if move was invalid" in {
      gc.setupPlayers(("P1", "1"), ("P2", "2"))
      val activeBefore = gc.getPlayer
      gc.makeMove(10)
      gc.getPlayer should be(activeBefore)
    }
  }
}