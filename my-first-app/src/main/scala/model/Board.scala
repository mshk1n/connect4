package model

class Board():
  val rows = 6
  val cols = 7
  
  val grid = Array.fill(rows, cols)(" ")

  def render(): String =
    val rowBorder = " --------------------------- \n"
    val colBorder = "|"
    val result = StringBuilder()
    for (row <- 0 until rows) {
      result.append(rowBorder)
      for (col <- 0 until cols) {
        result.append(s"$colBorder ${grid(row)(col)} ")
      }
      result.append(s"$colBorder\n")
    }
    result.append(rowBorder)
    result.toString()

  def getCell(row: Int, col: Int): String = grid(row)(col)

  def dropChip(col: Int, symbol: String): Option[(Int, Int)] =
    if (col < 0 || col >= cols) //check column range
      return None

    val maybeRow = (rows - 1 to 0 by -1).find(r => grid(r)(col) == " ")
    
    maybeRow match
    case Some(r) => grid(r)(col) = symbol //replace " " with chip
        Some((r, col))  //return coordinates of the new chip
    case None => None  //column is full

  //counting matching symbols in a direction
  private def countInDir(r: Int, c: Int, dr: Int, dc: Int, symbol: String): Int =
    val nextR = r + dr  //coordinate r (row) + step dr
    val nextC = c + dc  //coordinate c (column) + step dc
    
    //check boundaries and symbol match
    if (nextR >= 0 && nextR < rows && nextC >= 0 && nextC < cols && grid(nextR)(nextC) == symbol) {
      1 + countInDir(nextR, nextC, dr, dc, symbol) //move to the next cell
    } else {
      0 //stop if out of bounds or different symbol encountered
  }

  //check if the board is full
  def isFull: Boolean =
    !grid.exists(_.contains(" "))

  //check if there's a win
  def checkWin(row: Int, col: Int): Boolean =
    val symbol = grid(row)(col)
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
      val total = 1 + countInDir(row, col, dr, dc, symbol) + countInDir(row, col, -dr, -dc, symbol)
      total >= 4
    }