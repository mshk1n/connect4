package util

import scala.util.{Try, Success, Failure}

trait Command:
  def doStep(): Try[Unit]
  def undoStep(): Try[Unit]
  def redoStep(): Try[Unit]

class UndoManager:
  private var undoStack: List[Command] = Nil
  private var redoStack: List[Command] = Nil

  def doStep(command: Command): Try[Unit] =
    command.doStep() match
      case Success(_) =>
        undoStack = command :: undoStack
        redoStack = Nil
        Success(())
      case Failure(ex) => 
        Failure(ex)

  def undoStep(): Try[Unit] =
    undoStack match
      case Nil => 
        Failure(new IllegalStateException("Nothing to undo!"))
      case head :: stack =>
        head.undoStep() match
          case Success(_) =>
            undoStack = stack
            redoStack = head :: redoStack
            Success(())
          case Failure(ex) => 
            Failure(ex)

  def redoStep(): Try[Unit] =
    redoStack match
      case Nil => 
        Failure(new IllegalStateException("Nothing to redo!"))
      case head :: stack =>
        head.redoStep() match
          case Success(_) =>
            redoStack = stack
            undoStack = head :: undoStack
            Success(())
          case Failure(ex) => 
            Failure(ex)