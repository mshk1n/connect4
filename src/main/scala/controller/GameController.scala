package controller

import model._
import util.{Observable, UndoManager}
import scala.util.{Try, Success, Failure}

class GameController(val board: Board) extends Observable:
  private[controller] var players: List[Player] = Nil
  private val playerFactory: util.PlayerFactory = new util.HumanPlayerFactory
  private var winStrategy: util.WinStrategy = new util.ConnectNStrategy(4)
  private var currentState: GameState = new InitializationState(this)
  private val undoManager = new UndoManager

  def getWinStrategy: util.WinStrategy = winStrategy

  def setWinCount(n: Int): Unit =
    winStrategy = new util.ConnectNStrategy(n)

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