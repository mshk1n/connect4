case class Board():
  val rows = 6
  val cols = 7
  
  val grid = Array.fill(rows, cols)(" ")

  val rowBorder = " --------------------------- \n"
  val colBorder = "|"

  def printBoard(): String =
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

  def dropChip(col: Int, symbol: String): Unit =
    val maybeRow = (rows - 1 to 0 by -1).find(r => grid(r)(col) == " ")
    
    maybeRow match
    case Some(r) => grid(r)(col) = symbol
        print(printBoard())
    case None => println(ConsoleColors.RED("Column is full! Choose another."))