package view

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import controller.GameController
import model.Board
import scala.swing.event.ButtonClicked
import model.BoardInterface
import fileio.FileIOInterface

class MainGUISpec extends AnyWordSpec with Matchers {

  class TestBoard extends BoardInterface {
    val height: Int = 6
    val width: Int = 7
    // Implement missing members from BoardInterface
    override def cols: Int = width
    override def rows: Int = height
    override def setCell(row: Int, col: Int, value: String): Unit = {}

    override def getCell(row: Int, col: Int): String = " "
    override def dropChip(col: Int, symbol: String): Option[(Int, Int)] = Some((5, col))
    override def removeChip(row: Int, col: Int): Unit = {}
    override def isFull: Boolean = false
    override def render(): String = ""
  }

  "A MainGUI" should {

    "initialize with default components and window settings" in {
      val board = new TestBoard
      val fileIO: FileIOInterface = null.asInstanceOf[FileIOInterface]
      val gc = controller.GameControllerFactory.createControlller(board, fileIO)
      val gui = new MainGUI(gc)

      gui.title should be("Connect X")
      gui.player1Field.text should be("")
      gui.player2Field.text should be("")
      gui.radio4.selected should be(true)
    }

    "correctly read text fields and trigger state change when triggering PLAY button click" in {
      val board = new TestBoard
      val fileIO: FileIOInterface = null.asInstanceOf[FileIOInterface]
      val gc = controller.GameControllerFactory.createControlller(board, fileIO)
      val gui = new MainGUI(gc)

      gui.player1Field.text = "Alice"
      gui.player2Field.text = "Bob"
      gui.radio4.selected = false
      gui.radio5.selected = true

      gui.reactions.apply(ButtonClicked(gui.playButton))

      gc.getPlayer.name should be("Alice")
    }

    "correctly safe-guard components before cells array is fully built" in {
      val board = new TestBoard
      val fileIO: FileIOInterface = null.asInstanceOf[FileIOInterface]
      val gc = controller.GameControllerFactory.createControlller(board, fileIO)
      gc.setupPlayers(("Alice", "X"), ("Bob", "O"))
      
      val gui = new MainGUI(gc)

      gui.startGameUI()

      noException should be thrownBy {
        gui.update()
      }
    }

    "correctly process grid setup and color cell buttons after game starts" in {
      val board = new TestBoard
      val fileIO: FileIOInterface = null.asInstanceOf[FileIOInterface]
      val gc = controller.GameControllerFactory.createControlller(board, fileIO)
      
      gc.setupPlayers(("Player1", "X"), ("Player2", "O"))
      
      val gui = new MainGUI(gc)
      gui.startGameUI()

      gui.cells(0)(0) should not be null
    }
  }
}