package model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class PlayerSpec extends AnyWordSpec with Matchers {

  "A Player" should {
    
    "have a name and a symbol" in {
      val player = Player("Alice", "X")
      player.name should be("Alice")
      player.symbol should be("X")
    }

    "allow different names and symbols" in {
      val player = Player("Bob", "O")
      player.name should be("Bob")
      player.symbol should be("O")
    }

    "support equality based on its properties" in {
      val player1 = Player("Alice", "X")
      val player2 = Player("Alice", "X")
      val player3 = Player("Bob", "O")

      player1 should be(player2)
      player1 should not be player3
    }

    "have a proper string representation (toString)" in {
      val player = Player("Charlie", "Y")
      player.toString should be("Player(Charlie,Y)")
    }
  }
}