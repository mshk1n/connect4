package controller

import model._

class GameController(val board: Board):
  //create private list of observers
  private var observers: List[Observer] = Nil
  //create private list of player
  private var players: List[Player] = Nil
  private var currentPlayerIndex = 0

  //player-getter
  def getPlayer: Player = players(currentPlayerIndex)

  //add observers
  def add(s: Observer): Unit =
    observers = s :: observers
  
  //deliver events to observers
  def notifyObservers(): Unit =
    observers.foreach(_.update())

  //make a move
  def makeMove(col: Int): Boolean =
    val player = getPlayer
    val success = board.dropChip(col, player.coloredSymbol)
    if (success) then
        currentPlayerIndex = (currentPlayerIndex + 1) % players.length  //switch to the other player
        notifyObservers()
    success
  
  //creating players
  def setupPlayers(p1Data: (String, String), p2Data: (String, String)): Unit =
    players = List(
      Player(p1Data._1, p1Data._2, util.ConsoleColors.RED),
      Player(p2Data._1, p2Data._2, util.ConsoleColors.YELLOW)
    )

  //toString
  def boardToString: String =
    board.render()