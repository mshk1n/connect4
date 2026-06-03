package controller

import model._
import util.{Observable, UndoManager}
import scala.util.{Try, Success, Failure}

class GameController(val board: Board) extends Observable:
  //create private list of player
  private var players: List[Player] = Nil
  private[controller] var currentPlayerIndex = 0

  var isGameOver: Boolean = false
  var winner: Option[Player] = None

  //player-getter
  def getPlayer: Player = players(currentPlayerIndex)

  //playerSymbol-getter
  def getPlayerColoredSymbol(index: Int): String = players(index).coloredSymbol

  //undoManager
  private val undoManager = new UndoManager

  //make a move
  def makeMove(col: Int): Try[Unit] =
    if (isGameOver) then
      Failure(new IllegalStateException("Game is already over!"))
    else
      val result = undoManager.doStep(new InsertCommand(this, col))
      notifyObservers()
      result

  //undo a move
  def undo(): Option[Unit] =
    val result = undoManager.undoStep()
    notifyObservers()
    result

  //redo a move
  def redo(): Option[Try[Unit]] =
    val result = undoManager.redoStep()
    notifyObservers()
    result

  private[controller] def executeMoveLogic(col: Int): Option[Int] =
    val player = getPlayer
    board.dropChip(col, player.coloredSymbol) match
      case Some(row, c) =>
        if (board.checkWin(row, c)) then
          isGameOver = true
          winner = Some(player)
        else if (board.isFull) then
          isGameOver = true
          winner = None
        else
          currentPlayerIndex = (currentPlayerIndex + 1) % players.length
        Some(row)
      case None => 
        None

  private[controller] def undoGameStatus(previousPlayerIndex: Int): Unit =
    isGameOver = false
    winner = None
    currentPlayerIndex = previousPlayerIndex
  
  //creating players
  def setupPlayers(p1Data: (String, String), p2Data: (String, String)): Unit =
    players = List(
      Player(p1Data._1, p1Data._2, util.ConsoleColors.RED),
      Player(p2Data._1, p2Data._2, util.ConsoleColors.YELLOW)
    )

  //toString
  def boardToString: String =
    board.render()