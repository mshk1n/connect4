package view

import controller._
import util.ConsoleColors

import scala.io.StdIn.readLine

class TUI(gc: GameController) extends Observer:
  //add controller to observer list
  gc.add(this)

  //update-method from Observer
  override def update(): Unit =
    print(gc.boardToString)
    if (gc.isGameOver) {
      gc.winner match {
        case Some(p) => print(s"Congratulations! ${p.name} won!")
        case None    => print("It's a draw! Game over.")
      }
      println("Exiting...")
      sys.exit()
    }

  //processing an input from user
  def processInput(input: String): Unit =
    //Ctrl+Z case
    if input == null then
      print("\nExiting...")
      sys.exit()

    input.toIntOption match {
      case Some(col) => 
        if !gc.makeMove(col) then
          print(ConsoleColors.RED("Error! Invalid column or column is full."))
      case None => 
        print(ConsoleColors.RED("Error! Please type a number."))
    }
  
  //registering a player
  def registerPlayer(playerNumber: Int): (String, String) = 
    print(s"==== Register Player $playerNumber... ====\n")
    val name = askForName()
    val symbol = askForSymbol(name)
    (name, symbol)

  //processing name input
  private def askForName(): String =
    print("Enter your name: \n")
    val nameInput = readLine()
    if nameInput == null then
      print(ConsoleColors.RED("Error! Name cannot be empty. Try again :\n"))
      askForName()
    else
      nameInput

  //processing chip input
  private def askForSymbol(name: String): String =
    print(s"Now, $name, enter your symbol: ")
    val chipInput = readLine()
    if chipInput == null then
      print(ConsoleColors.RED("Error! Chip cannot be empty! Try again: \n"))
      askForSymbol(name)
    else if chipInput.length() != 1 then
      print(ConsoleColors.RED("Error! Chip must contain only 1 symbol! Try again: \n"))
      askForSymbol(name)
    else
      chipInput