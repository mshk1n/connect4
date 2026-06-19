package controller

import scala.util.{Try, Success, Failure}
import model.Player

trait GameState:
  def makeMove(col: Int): Try[Unit]
  def currentPlayer: Option[Player]

class InitializationState(controller: GameController) extends GameState:
  override def makeMove(col: Int): Try[Unit] = 
    Failure(new IllegalStateException("Game has not started yet! Setup players first."))
  override def currentPlayer: Option[Player] = None

class PlayerTurnState(controller: GameController, val playerIndex: Int) extends GameState:
  
  override def currentPlayer: Option[Player] = 
    if (controller.players.nonEmpty) 
      Some(controller.players(playerIndex)) 
    else 
      None

  override def makeMove(col: Int): Try[Unit] =
    currentPlayer match
      case None => 
        Failure(new IllegalStateException("Players are not initialized!"))
      case Some(player) =>
        controller.executeMoveLogic(col) match
          case Some((actualRow, actualCol)) =>
            
            if (controller.getWinStrategy.checkWin(controller.board, actualRow, actualCol)) then
              controller.changeState(new GameOverState(controller, Some(player)))
            
            else if (controller.board.isFull) then
              controller.changeState(new GameOverState(controller, None))
            
            else
              val nextIndex = (playerIndex + 1) % controller.players.length
              controller.changeState(new PlayerTurnState(controller, nextIndex))
            
            Success(())
            
          case None => 
            Failure(new IllegalArgumentException(s"Column $col is full! Choose another one."))

class GameOverState(controller: GameController, val winner: Option[Player]) extends GameState:
  override def makeMove(col: Int): Try[Unit] = 
    Failure(new IllegalStateException("Game is already over! Click restart to play again."))
  override def currentPlayer: Option[Player] = None