package controller

import model._
import util.Observable

class GameController(val board: Board) extends Observable:
  //create private list of player
  private var players: List[Player] = Nil
  private var currentPlayerIndex = 0

  var isGameOver: Boolean = false
  var winner: Option[Player] = None

  //player-getter
  def getPlayer: Player = players(currentPlayerIndex)

  //make a move
  def makeMove(col: Int): Boolean =
    if (isGameOver) return false //prevent moves after the game ends

    val player = getPlayer
    // Using pattern matching to handle the Option returned by Board
    board.dropChip(col, player.coloredSymbol) match {
      case Some(row, c) =>
        if (board.checkWin(row, c)) {
          isGameOver = true
          winner = Some(player) //win
        } else if (board.isFull) {
          isGameOver = true
          winner = None //draw
        } else {
          //no win or draw - switch to the next player
          currentPlayerIndex = (currentPlayerIndex + 1) % players.length
        }
        notifyObservers()
        true
      case None => 
        false //move failed (column full or invalid)
    }
  
  //creating players
  def setupPlayers(p1Data: (String, String), p2Data: (String, String)): Unit =
    players = List(
      Player(p1Data._1, p1Data._2, util.ConsoleColors.RED),
      Player(p2Data._1, p2Data._2, util.ConsoleColors.YELLOW)
    )

  //toString
  def boardToString: String =
    board.render()