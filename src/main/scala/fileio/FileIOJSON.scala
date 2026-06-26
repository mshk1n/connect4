package fileio

import model.{BoardInterface, Player, BoardFactory}
import controller.{GameState, PlayerTurnState}
import java.io._
import scala.util.Try
import play.api.libs.json._

class FileIOJSON extends FileIOInterface {
  
  private val filename = "game.json"

  override def save(board: BoardInterface, players: List[Player], state: GameState, winCount: Int): Try[Unit] = Try {
    val playerIndex = state match {
      case pts: PlayerTurnState => pts.playerIndex
      case _ => 0
    }

    val playersJson = Json.toJson(
      players.map(p => Json.obj("name" -> p.name, "symbol" -> p.symbol))
    )

    val cellsJson = Json.toJson(
      for {
        r <- 0 until board.rows
        c <- 0 until board.cols
      } yield Json.obj(
        "row" -> r,
        "col" -> c,
        "value" -> board.getCell(r, c)
      )
    )

    val json = Json.obj(
      "winCount" -> winCount,
      "currentPlayerIndex" -> playerIndex,
      "players" -> playersJson,
      "board" -> Json.obj(
        "rows" -> board.rows,
        "cols" -> board.cols,
        "cells" -> cellsJson
      )
    )

    val pw = new PrintWriter(new File(filename))
    pw.write(Json.prettyPrint(json))
    pw.close()
  }

  override def load: Try[(BoardInterface, List[Player], GameState, Int)] = Try {
    val file = new java.io.File(filename)
    
    val source = scala.io.Source.fromFile(file)
    val jsonString = source.getLines().mkString
    source.close()
    
    val json = Json.parse(jsonString)

    val winCount = (json \ "winCount").as[Int]
    val playerIndex = (json \ "currentPlayerIndex").as[Int]

    val loadedPlayers = (json \ "players").as[List[JsValue]].map { p =>
      Player((p \ "name").as[String], (p \ "symbol").as[String])
    }

    val board = BoardFactory.createBoard()
    val cells = (json \ "board" \ "cells").as[List[JsValue]]
    
    for (cell <- cells) {
      val r = (cell \ "row").as[Int]
      val c = (cell \ "col").as[Int]
      val value = (cell \ "value").as[String]
      board.setCell(r, c, value)
    }

    val dummyState = new PlayerTurnState(null, playerIndex)

    (board, loadedPlayers, dummyState, winCount)
  }
}