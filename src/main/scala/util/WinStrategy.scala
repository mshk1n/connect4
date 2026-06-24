package util

import model.BoardInterface

trait WinStrategy:
    def checkWin(board: BoardInterface, row: Int, col: Int): Boolean
    def winCount: Int

class ConnectNStrategy(val winCount: Int) extends WinStrategy:
    override def checkWin(board: BoardInterface, row: Int, col: Int): Boolean =
        val symbol = board.getCell(row, col)
        if (symbol == " ") 
          return false

        //directions
        val axes = List(
          (0, 1),  //horizontal axis
          (1, 0),  //vertical axis
          (1, 1),  //diagonal axis (top-left to bottom-right)
          (1, -1)  //diagonal axis (top-right to bottom-left)
        )

        //for each axis, count identical symbols in both directions
        axes.exists { case (dr, dc) =>
          //current chip (1) + matches in positive direction + matches in negative direction
          val total = 1 + countInDir(board, row, col, dr, dc, symbol) + countInDir(board, row, col, -dr, -dc, symbol)
          total >= winCount
        }

        //counting matching symbols in a direction
    private def countInDir(board: BoardInterface, r: Int, c: Int, dr: Int, dc: Int, symbol: String): Int =
        val nextR = r + dr  //coordinate r (row) + step dr
        val nextC = c + dc  //coordinate c (column) + step dc
    
        //check boundaries and symbol match
        if (nextR >= 0 && nextR < 6 && nextC >= 0 && nextC < 7 && board.getCell(nextR, nextC) == symbol) {
          1 + countInDir(board, nextR, nextC, dr, dc, symbol) //move to the next cell
        } else {
          0 //stop if out of bounds or different symbol encountered
        }