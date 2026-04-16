case class Board():
    val rowAmount = 7
    val colAmount = 8

    val rowBorder = " ------------------------- \n"
    val colBorder = " | "

    def printBoard(): String = {
        val result = new StringBuilder()
        for (i <- 0 to rowAmount) {
            result.append(rowBorder)
            for (i <- 0 to colAmount) {
                result.append(colBorder)
            }
            result.append("\n")
        }
        result.toString()
    }