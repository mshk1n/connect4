import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class ConsoleColorsSpec extends AnyWordSpec with Matchers {

  "ConsoleColors" should {
    "format text with colors" in {
    val coloredText = ConsoleColors.RED("Hello")
    coloredText should include ("Hello")
    coloredText should startWith ("\u001B[31m")
    }
  }
}