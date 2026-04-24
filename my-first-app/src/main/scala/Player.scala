import scala.io.StdIn.readLine

case class Player(name: String, symbol: String, color: ConsoleColors):
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