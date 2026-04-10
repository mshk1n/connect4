case class Board():
    val rowAmount = 7
    val colAmount = 8

    val rowBorder = " --------------------- \n"
    val colBorder = " | "
    for (i <- 0 to rowAmount) {
        print(rowBorder)
        for (i <- 0 to colAmount) {
            print(colBorder)
        }
    }
    val mergeTest = 10