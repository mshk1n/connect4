package util

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import scala.util.{Try, Success, Failure}

// Специфичная тестовая команда для проверки логики UndoManager
class TestCommand extends Command {
  var doCalled = 0
  var undoCalled = 0
  var redoCalled = 0
  var failOnDo = false

  override def doStep(): Try[Unit] = 
    if (failOnDo) Failure(new IllegalArgumentException("Simulated error"))
    else { doCalled += 1; Success(()) }

  override def undoStep(): Try[Unit] = { undoCalled += 1; Success(()) }
  override def redoStep(): Try[Unit] = { redoCalled += 1; Success(()) }
}

class UndoManagerSpec extends AnyWordSpec with Matchers {

  "An UndoManager" should {

    "successfully execute a command and store it in the undo stack" in {
      val manager = new UndoManager
      val command = new TestCommand

      val result = manager.doStep(command)
      result.isSuccess should be(true)
      command.doCalled should be(1)
    }

    "return a Failure if the command execution fails" in {
      val manager = new UndoManager
      val command = new TestCommand
      command.failOnDo = true

      val result = manager.doStep(command)
      result.isFailure should be(true)
      command.doCalled should be(0)
    }

    "correctly undo a previously executed command" in {
      val manager = new UndoManager
      val command = new TestCommand

      manager.doStep(command)
      val undoResult = manager.undoStep()

      undoResult.isSuccess should be(true)
      command.undoCalled should be(1)
    }

    "return a Failure when attempting to undo with an empty stack" in {
      val manager = new UndoManager
      
      val result = manager.undoStep()
      result.isFailure should be(true)
      result.failed.get.getMessage should be("Nothing to undo!")
    }

    "correctly redo a previously undone command" in {
      val manager = new UndoManager
      val command = new TestCommand

      manager.doStep(command)
      manager.undoStep()
      val redoResult = manager.redoStep()

      redoResult.isSuccess should be(true)
      command.redoCalled should be(1)
    }

    "return a Failure when attempting to redo with an empty redo stack" in {
      val manager = new UndoManager
      
      val result = manager.redoStep()
      result.isFailure should be(true)
      result.failed.get.getMessage should be("Nothing to redo!")
    }

    "clear the redo stack when a new command is executed" in {
      val manager = new UndoManager
      val command1 = new TestCommand
      val command2 = new TestCommand

      manager.doStep(command1)
      manager.undoStep()
      
      manager.doStep(command2)
      
      val redoResult = manager.redoStep()
      redoResult.isFailure should be(true)
    }
  }
}