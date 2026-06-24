package fileio

import model.{BoardInterface, Player, BoardFactory}
import controller.{GameState, PlayerTurnState}
import java.io._
import scala.xml._
import scala.util.Try

class FileIOXML extends FileIOInterface {
  private val filename = "game.xml"

  override def save(board: BoardInterface, players: List[Player], state: GameState, winCount: Int): Try[Unit] = Try {
    val playerIndex = state match {
      case pts: PlayerTurnState => pts.playerIndex
      case _ => 0
    }

    val xml = 
      <game winCount={winCount.toString} currentPlayerIndex={playerIndex.toString}>
        <players>
          {
            for (p <- players) yield {
              <player name={p.name} symbol={p.symbol}/>
            }
          }
        </players>
        <board rows={board.rows.toString} cols={board.cols.toString}>
          <grid>
            {
              for {
                r <- 0 until board.rows
                c <- 0 until board.cols
              } yield {
                <cell row={r.toString} col={c.toString}>{board.getCell(r, c)}</cell>
              }
            }
          </grid>
        </board>
      </game>

    val pp = new PrettyPrinter(80, 2)
    val pw = new PrintWriter(new File(filename))
    pw.write(pp.format(xml))
    pw.close()
  }

  override def load: Try[(BoardInterface, List[Player], GameState, Int)] = Try {
    val file = new java.io.File(filename)
    val xml = XML.loadFile(file)

    val winCount = (xml \ "@winCount").text.toInt
    val playerIndex = (xml \ "@currentPlayerIndex").text.toInt
    
    val playerNodes = xml \ "players" \ "player"
    val loadedPlayers = playerNodes.map { p =>
      Player((p \ "@name").text, (p \ "@symbol").text)
    }.toList

    val board = BoardFactory.createBoard()
    val cellNodes = xml \\ "cell"
    for (cell <- cellNodes) {
      val r = (cell \ "@row").text.toInt
      val c = (cell \ "@col").text.toInt
      board.setCell(r, c, cell.text)
    }

    val dummyState = new PlayerTurnState(null, playerIndex)

    (board, loadedPlayers, dummyState, winCount)
  }
}