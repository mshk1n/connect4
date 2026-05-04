//col 0   col 1   col 2   col 3   col 4   col 5   col 6
//row 0 [ 0,0 ] [ 0,1 ] [ 0,2 ] [ 0,3 ] [ 0,4 ] [ 0,5 ] [ 0,6 ]  <-- FIRST ROW
//row 1 [ 1,0 ] [ 1,1 ] [ 1,2 ] [ 1,3 ] [ 1,4 ] [ 1,5 ] [ 1,6 ]
//row 2 [ 2,0 ] [ 2,1 ] [ 2,2 ] [ 2,3 ] [ 2,4 ] [ 2,5 ] [ 2,6 ]
//row 3 [ 3,0 ] [ 3,1 ] [ 3,2 ] [ 3,3 ] [ 3,4 ] [ 3,5 ] [ 3,6 ]
//row 4 [ 4,0 ] [ 4,1 ] [ 4,2 ] [ 4,3 ] [ 4,4 ] [ 4,5 ] [ 4,6 ]
//row 5 [ 5,0 ] [ 5,1 ] [ 5,2 ] [ 5,3 ] [ 5,4 ] [ 5,5 ] [ 5,6 ]  <-- LAST ROW

  def coloredSymbol: String = color(symbol.toString)
  
object Player {
  def registerPlayer(playerNumber: Int): Player =
    print(s"==== Register Player ${playerNumber}... ====\n")
    print("Enter your name: \n")
    val nameInput = createName()

    print(s"Now, ${nameInput}, enter the symbol you are going to use as your gaming chip: \n")
    val chip = createChip()
    val colorInput = if (playerNumber == 1) then
      ConsoleColors.RED
    else
      ConsoleColors.YELLOW
    new Player(nameInput, chip, colorInput)
  
  private def createName(): String =
    val nameInput = readLine()
    if nameInput == null then
      print(ConsoleColors.RED("Error! Name cannot be empty. Try again :\n"))
      createName()
    else
      nameInput

  private def createChip(): String = 
    val chipInput = readLine()
    if chipInput == null then
      print(ConsoleColors.RED("Error! Chip cannot be empty! Try again: \n"))
      createChip()
    else if chipInput.length() != 1 then
      print(ConsoleColors.RED("Error! Chip must contain only 1 symbol! Try again: \n"))
      createChip()
    else
      chipInput
}