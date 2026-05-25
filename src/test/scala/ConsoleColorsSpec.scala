package util

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class ConsoleColorsSpec extends AnyWordSpec with Matchers {
  "ConsoleColors" should {
    "provide the correct ANSI codes" in {
      ConsoleColors.RED.code should be("\u001B[31m")
      ConsoleColors.CLEAR.code should be("\u001B[0m")
    }

    "wrap text correctly using the apply method" in {
      val greenText = ConsoleColors.GREEN("Hello")
      greenText should be("\u001B[32mHello\u001B[0m")
    }
  }
}