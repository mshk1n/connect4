package util

import scala.util.{Try, Success, Failure}

trait Command:
  def doStep(): Try[Unit]
  def undoStep(): Unit
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

  def undoStep(): Option[Unit] =
    undoStack match
      case Nil => None
      case head :: stack =>
        head.undoStep()
        undoStack = stack
        redoStack = head :: redoStack
        Some(())

  def redoStep(): Option[Try[Unit]] =
    redoStack match
      case Nil => None
      case head :: stack =>
        head.redoStep() match
          case Success(_) =>
            redoStack = stack
            undoStack = head :: undoStack
            Some(Success(()))
          case Failure(ex) => 
            Some(Failure(ex))