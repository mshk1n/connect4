import scala.io.StdIn.readLine

@main def game(): Unit =

  val p1 = Player.registerPlayer(1)
  val board = Board()

  print(s"Welcome, ${p1.name}! Game begins now! To exit, press Ctrl+Z.\n")
  print(board.printBoard())

  while(true) do
    
    print(s"${p1.name}, choose a column (0 - 6): ")

    val input = readLine()

    if input == null then
      print("\nExiting...")
      sys.exit()

    input.toIntOption match {
      case Some(col) if col >= 0 && col <= board.cols => board.dropChip(col, p1.coloredSymbol)
      case _ => print(ConsoleColors.RED("Error! Type a valid column number.\n"))
    }