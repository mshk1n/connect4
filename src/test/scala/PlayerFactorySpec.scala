package util

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import model.Player

class PlayerFactorySpec extends AnyWordSpec with Matchers {

  "A HumanPlayerFactory" should {

    "create a Player instance with the exact name and symbol provided" in {
      val factory: PlayerFactory = new HumanPlayerFactory
      val player = factory.createPlayer("Alice", "X")

      player shouldNot be(null)
      player.name should be("Alice")
      player.symbol should be("X")
    }

    "correctly handle different player data" in {
      val factory: PlayerFactory = new HumanPlayerFactory
      val player = factory.createPlayer("Bob", "O")

      player.name should be("Bob")
      player.symbol should be("O")
    }

    "return a valid Player case class instance" in {
      val factory: PlayerFactory = new HumanPlayerFactory
      val player = factory.createPlayer("Charlie", "▲")

      player shouldBe a [Player]
    }
  }
}