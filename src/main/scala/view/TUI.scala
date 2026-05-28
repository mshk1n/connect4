package view

import controller._
import util.ConsoleColors
import scala.util.{Success, Failure}

import scala.io.StdIn.readLine

class TUI(gc: GameController) extends Observer:
  //add controller to observer list
  gc.add(this)

  //update-method from Observer
  override def update(): Unit =
    print(gc.boardToString)
    if (gc.isGameOver) {
      gc.winner match {
        case Some(p) => println(s"\nCongratulations! ${p.name} won!")
        case None    => println("\nIt's a draw! Game over.")
      }
      println("Exiting...")
    }

  //processing an input from user
  def processInput(input: String): Unit =
    //Ctrl+Z case
    if input == null then
      print("\nExiting...")
      sys.exit()

    input.trim.toLowerCase match {
      //undo-button
      case "z" => 
        gc.undo() match {
          case Some(_) => println("Undo successful.")
          case None => print(ConsoleColors.RED("Nothing to undo!\n"))
        }

      //redo-button
      case "y" => 
        gc.redo() match {
          case Some(Success(_)) => println("Redo successful.")
          case Some(Failure(ex)) => print(ConsoleColors.RED(s"Error on redo: ${ex.getMessage}\n"))
          case None => print(ConsoleColors.RED("Nothing to redo!\n"))
        }

      //any other button
      case other => 
        other.toIntOption match {
          case Some(col) => 
            gc.makeMove(col) match {
              case Success(_) => 
              case Failure(exception) => 
                print(ConsoleColors.RED(s"Error! ${exception.getMessage}\n"))
            }
          case None => 
            print(ConsoleColors.RED("Error! Please type a number, 'z' (undo), or 'y' (redo).\n"))
        }
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