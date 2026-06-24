package model

trait BoardInterface:
  def rows: Int
  def cols: Int
  def render(): String
  def getCell(row: Int, col: Int): String
  def dropChip(col: Int, symbol: String): Option[(Int, Int)]
  def isFull: Boolean
  def removeChip(row: Int, col: Int): Unit
  def setCell(row: Int, col: Int, value: String): Unit

class Board() extends BoardInterface:
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

  //check if the board is full
  def isFull: Boolean =
    !grid.exists(_.contains(" "))
  
  //remove chip
  def removeChip(row: Int, col: Int): Unit =
    if (row >= 0 && row < rows && col >= 0 && col < cols) then
      grid(row)(col) = " "
    
  def setCell(row: Int, col: Int, value: String): Unit =
    if (row >= 0 && row < rows && col >= 0 && col < cols) {
      grid(row)(col) = value
    }

object BoardFactory:
  def createBoard(): BoardInterface = new Board()