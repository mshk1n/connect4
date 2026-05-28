package controller

import util.Command
import scala.util.{Try, Success, Failure}

class InsertCommand(controller: GameController, col: Int) extends Command:
  //save coordinates in case of remove command
  private var placedRow: Option[Int] = None
  
  //save current player's index
  private var savedPlayerIndex: Int = controller.currentPlayerIndex

  override def doStep(): Try[Unit] =
    controller.executeMoveLogic(col) match
      case Some(row) =>
        placedRow = Some(row) //save row
        Success(())
      case None =>
        //column is full or wrong number
        Failure(new IllegalArgumentException(s"Column $col is full or invalid!"))

  override def undoStep(): Unit =
    placedRow.foreach { row =>
      controller.board.removeChip(row, col)
      controller.undoGameStatus(savedPlayerIndex)
      placedRow = None
    }

  override def redoStep(): Try[Unit] =
    doStep()