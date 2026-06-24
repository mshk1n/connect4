package fileio

import model.{BoardInterface, Player}
import controller.GameState
import scala.util.Try

trait FileIOInterface {
    def load: Try[(BoardInterface, List[Player], GameState, Int)]
    def save(board: BoardInterface, players: List[Player], state: GameState, winCount: Int): Try[Unit]
}