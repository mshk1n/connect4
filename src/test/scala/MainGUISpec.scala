package view

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import controller.GameController
import model.Board
import scala.swing.event.ButtonClicked

class MainGUISpec extends AnyWordSpec with Matchers {

  class TestBoard extends Board {
    val height: Int = 6
    val width: Int = 7
    override def getCell(row: Int, col: Int): String = " "
    override def dropChip(col: Int, symbol: String): Option[(Int, Int)] = Some((5, col))
    override def removeChip(row: Int, col: Int): Unit = {}
    override def isFull: Boolean = false
    override def render(): String = ""
  }

  "A MainGUI" should {

    "initialize with default components and window settings" in {
      val board = new TestBoard
      val controller = new GameController(board)
      val gui = new MainGUI(controller)

      gui.title should be("Connect X")
      gui.player1Field.text should be("")
      gui.player2Field.text should be("")
      gui.radio4.selected should be(true)
    }

    "correctly read text fields and trigger state change when triggering PLAY button click" in {
      val board = new TestBoard
      val controller = new GameController(board)
      val gui = new MainGUI(controller)

      gui.player1Field.text = "Alice"
      gui.player2Field.text = "Bob"
      gui.radio4.selected = false
      gui.radio5.selected = true

      gui.reactions.apply(ButtonClicked(gui.playButton))

      controller.getPlayer.name should be("Alice")
    }

    "correctly safe-guard components before cells array is fully built" in {
      val board = new TestBoard
      val controller = new GameController(board)
      controller.setupPlayers(("Alice", "X"), ("Bob", "O"))
      
      val gui = new MainGUI(controller)

      gui.startGameUI()

      noException should be thrownBy {
        gui.update()
      }
    }

    "correctly process grid setup and color cell buttons after game starts" in {
      val board = new TestBoard
      val controller = new GameController(board)
      
      controller.setupPlayers(("Player1", "X"), ("Player2", "O"))
      
      val gui = new MainGUI(controller)
      gui.startGameUI()

      gui.cells(0)(0) should not be null
    }
  }
}