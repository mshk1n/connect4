package controller

import util.Command
import scala.util.{Try, Success, Failure}

class InsertCommand(controller: GameController, col: Int) extends Command:
  private var placedRow: Option[Int] = None
  private var savedState: GameState = controller.getCurrentState

  override def doStep(): Try[Unit] =
    controller.getCurrentState.makeMove(col) match
      case Success(()) =>
        placedRow = controller.getPlacedRowOfColumn(col)
        Success(())
      case Failure(ex) =>
        Failure(ex)

  override def undoStep(): Try[Unit] =
    placedRow match
      case Some(row) =>
        controller.board.removeChip(row, col)
        controller.undoGameStatus(savedState)
        placedRow = None
        Success(())
      case None =>
        Failure(new IllegalStateException("No move to undo!"))

  override def redoStep(): Try[Unit] =
    savedState = controller.getCurrentState
    doStep()