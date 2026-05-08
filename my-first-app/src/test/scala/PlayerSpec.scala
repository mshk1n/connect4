package model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import util.ConsoleColors

class PlayerSpec extends AnyWordSpec with Matchers {
  "A Player" should {
    val player = Player("Alice", "X", ConsoleColors.RED)

    "have a name, symbol and color" in {
      player.name should be("Alice")
      player.symbol should be("X")
      player.color should be(ConsoleColors.RED)
    }

    "return a correctly formatted colored symbol" in {
      val expected = s"${ConsoleColors.RED.code}X${ConsoleColors.CLEAR.code}"
      player.coloredSymbol should be(expected)
    }
  }
}