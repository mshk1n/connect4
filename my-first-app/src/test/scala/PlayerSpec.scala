import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class PlayerSpec extends AnyWordSpec with Matchers {
    "A Player" should {
        val player = Player("Alice", "O", ConsoleColors.RED)

        "have a name" in {
            player.name shouldBe "Alice"
        }

        "have a symbol" in {
            player.symbol shouldBe "O"
        }

        "have a color" in {
            player.color shouldBe ConsoleColors.RED
        }
    }
}