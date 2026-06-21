package controller

import model._
import util.{Observable, UndoManager}
import scala.util.{Try, Success, Failure}
import com.google.inject.Inject

trait GameControllerInterface extends Observable:
  def getWinStrategy: util.WinStrategy
  def setWinCount(n: Int): Unit
  def isGameOver: Boolean
  def winner: Option[Player]
  def getPlayer: Player
  def getPlayerSymbol(index: Int): String
  def setupPlayers(p1Data: (String, String), p2Data: (String, String)): Unit
  def makeMove(col: Int): Try[Unit]
  def undo(): Try[Unit]
  def redo(): Try[Unit]
  def boardToString: String

  def getBoard: model.BoardInterface

class GameController @Inject() (val board: BoardInterface) extends GameControllerInterface:
  private[controller] var players: List[Player] = Nil
  private val playerFactory: util.PlayerFactory = new util.HumanPlayerFactory
  private var winStrategy: util.WinStrategy = new util.ConnectNStrategy(4)
  private var currentState: GameState = new InitializationState(this)
  private val undoManager = new UndoManager

  def getWinStrategy: util.WinStrategy = winStrategy

  def setWinCount(n: Int): Unit =
    winStrategy = new util.ConnectNStrategy(n)

  def getBoard: model.BoardInterface = board

  private[controller] def changeState(state: GameState): Unit =
    currentState = state

  private[controller] def getCurrentState: GameState = currentState

  def isGameOver: Boolean = currentState.isInstanceOf[GameOverState]
  
  def winner: Option[Player] = currentState match
    case go: GameOverState => go.winner
    case _ => None

  def getPlayer: Player = currentState.currentPlayer match
    case Some(p) => p
    case None    => Player("Unknown", "?")

  def getPlayerSymbol(index: Int): String = players(index).symbol

  def setupPlayers(p1Data: (String, String), p2Data: (String, String)): Unit =
    players = List(
      playerFactory.createPlayer(p1Data._1, p1Data._2),
      playerFactory.createPlayer(p2Data._1, p2Data._2)
    )
    changeState(new PlayerTurnState(this, 0))

  def makeMove(col: Int): Try[Unit] =
    val command = new InsertCommand(this, col)
    val result = undoManager.doStep(command)
    if (result.isSuccess) 
      notifyObservers()
    result

  private[controller] def executeMoveLogic(col: Int): Option[(Int, Int)] =
    val player = getPlayer
    board.dropChip(col, player.symbol) match
      case Some(actualRow, actualCol) => Some((actualRow, actualCol))
      case None                       => None

  private[controller] def getPlacedRowOfColumn(col: Int): Option[Int] =
    (0 until 6).find(r => board.getCell(r, col) != " ") 

  private[controller] def undoGameStatus(previousState: GameState): Unit =
    currentState = previousState

  def undo(): Try[Unit] =
    val result = undoManager.undoStep()
    if (result.isSuccess) notifyObservers()
    result

  def redo(): Try[Unit] =
    val result = undoManager.redoStep()
    if (result.isSuccess) notifyObservers()
    result

  def boardToString: String =
    board.render()

object GameControllerFactory:
  def createControlller(board: BoardInterface): GameControllerInterface = new GameController(board)