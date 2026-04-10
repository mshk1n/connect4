  enum ConsoleColors(val code: String) {
    case CLEAR extends ConsoleColors("\u001B[0m")
    case RED extends ConsoleColors("\u001B[31m")
    case GREEN extends ConsoleColors("\u001B[32m")
    case YELLOW extends ConsoleColors("\u001B[33m")
    case BLUE extends ConsoleColors("\u001B[34m")
    case PURPLE extends ConsoleColors("\u001B[35m")
    case CYAN extends ConsoleColors("\u001B[36m")
    case WHITE extends ConsoleColors("\u001B[37m")

    def apply(text: String): String = s"$code$text${CLEAR.code}"
  }

  
  case class Player(name: String, symbol: String, color: ConsoleColors)
  
  val p1 = new Player("Player1", "O", ConsoleColors.RED)
  val p2 = new Player("Player2", "O", ConsoleColors.YELLOW)
  val p1Chip = p1.color(p1.symbol)
  val p2Chip = p2.color(p2.symbol)

  val field: String =
    s"""---------------------
       ||  |  |  |  |  |  |  |
       |---------------------
       ||  |  |  |  |  |  |  |
       |---------------------
       ||  |  |  |  |  |  |  |
       |---------------------
       ||  |  |  |$p1Chip  |  |  |  |
       |---------------------
       ||  |  |$p1Chip  |  |  |  |  |
       |---------------------
       ||  |$p1Chip  |  |  |  |  |  |
       |---------------------
       ||$p1Chip |$p2Chip  |$p2Chip  |$p2Chip  |  |  |  |
       |---------------------
       |""".stripMargin

  print(field)