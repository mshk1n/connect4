package util

import model.Player

trait PlayerFactory:
    def createPlayer(name: String, symbol: String): Player

class HumanPlayerFactory extends PlayerFactory:
    override def createPlayer(name: String, symbol: String): Player =
        Player(name, symbol)