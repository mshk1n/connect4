package view

import controller._
import swing._
import scala.swing.event._
import scala.util.{Success, Failure}
import java.awt.event.ActionEvent

class MainGUI(controller: GameController) extends MainFrame with Observer:
  controller.add(this)
  val cells = Array.ofDim[Button](6, 7)

  title = "Connect X"
  preferredSize = new java.awt.Dimension(400, 400)

  var secondsElapsed = 0
  val timerLabel = new Label("Time - 00:00") {
    font = new java.awt.Font("Arial", java.awt.Font.BOLD, 14)
  }
  val timer = new javax.swing.Timer(1000, new java.awt.event.ActionListener {
    override def actionPerformed(e: ActionEvent): Unit = {
      secondsElapsed += 1
      val mins = secondsElapsed / 60
      val secs = secondsElapsed % 60
      timerLabel.text = f"Time - $mins%02d:$secs%02d"
    }
  })
  val turnLabel = new Label() {
    font = new java.awt.Font("Arial", java.awt.Font.BOLD, 14)
  }

  val titleLabel = new Label("Welcome to Connect X!")
  val player1Field = new TextField { columns = 15; preferredSize = new java.awt.Dimension(180, 26) }
  val vsLabel = new Label("==== VS ====")
  val player2Field = new TextField { columns = 15; preferredSize = new java.awt.Dimension(180, 26) }

  val modeLabel = new Label("Choose playing mode:")
  val radio3 = new RadioButton("Connect 3")
  val radio4 = new RadioButton("Connect 4") { selected = true }
  val radio5 = new RadioButton("Connect 5")
  val modeGroup = new ButtonGroup(radio3, radio4, radio5)

  val optionsPanel = new BoxPanel(Orientation.Horizontal) {
    contents += modeLabel
    contents += radio3
    contents += radio4
    contents += radio5
  }

  val playButton = new Button("PLAY") { preferredSize = new java.awt.Dimension(100, 35) }

  val undoButton = new Button("UNDO") { preferredSize = new java.awt.Dimension(80, 25) }
  val redoButton = new Button("REDO") { preferredSize = new java.awt.Dimension(80, 25) }

  contents = new BoxPanel(Orientation.Vertical) {
    border = Swing.EmptyBorder(15, 15, 15, 15)
    
    contents += Swing.VStrut(10)
    contents += new FlowPanel(titleLabel)
    contents += Swing.VStrut(5)
    contents += new FlowPanel(new Label("Player 1 Name:"))
    contents += new FlowPanel(player1Field)
    contents += Swing.VStrut(5)
    contents += new FlowPanel(vsLabel)
    contents += Swing.VStrut(5)
    contents += new FlowPanel(new Label("Player 2 Name:"))
    contents += new FlowPanel(player2Field)
    contents += Swing.VStrut(10)
    contents += new FlowPanel(modeLabel)
    contents += Swing.VStrut(5)
    contents += optionsPanel
    contents += Swing.VStrut(10)
    contents += new FlowPanel(playButton)
  }

  listenTo(playButton, undoButton, redoButton, radio3, radio4, radio5)
  reactions += {
    case ButtonClicked(`playButton`) =>
      if (player1Field.text.trim.isEmpty || player2Field.text.trim.isEmpty) then
        Dialog.showMessage(
            parent = this,
            message = "Player's name cannot be empty!",
            title = "Error!",
            messageType = Dialog.Message.Error,
        )
      else
        if (radio3.selected)
          controller.setWinCount(3)
        else if (radio5.selected)
          controller.setWinCount(5)
        else
          controller.setWinCount(4)
        controller.setupPlayers((player1Field.text, "X"), (player2Field.text, "O"))
        print("[DEBUG] Game Started!")
        startGameUI()
      print("[DEBUG] PLAY clicked!")
    
    case ButtonClicked(`undoButton`) =>
      controller.undo() match {
        case Success(_) => print("[DEBUG] Undo successful")
        case Failure(ex) => 
          Dialog.showMessage(parent = this, message = ex.getMessage, title = "Notice")
          print(s"[DEBUG] Undo failed! ${ex.getMessage()}")
      }

    case ButtonClicked(`redoButton`) =>
      controller.redo() match {
        case Success(_)  => print("[DEBUG] Redo successful")
        case Failure(ex) => 
          Dialog.showMessage(parent = this, message = ex.getMessage, title = "Notice")
          print(s"[DEBUG] Redo failed! ${ex.getMessage()}")
      } 
  }

  
  def startGameUI(): Unit = {
    this.preferredSize = new java.awt.Dimension(700, 550)
    turnLabel.text = s"${controller.getPlayer.name}'s move >>> "
    val gridPanel = new GridPanel(6, 7) {
      hGap = 5
      vGap = 5
      for (row <- 0 until 6) {
        for (col <- 0 until 7) {
          val chip = new Button {
            preferredSize = new java.awt.Dimension(70, 70)
            opaque = true
            peer.setContentAreaFilled(true)
            reactions += {
            case ButtonClicked(_) =>
              controller.makeMove(col) match {
                case Failure(ex) =>
                  Dialog.showMessage(
                    parent = MainGUI.this,
                    message = ex.getMessage,
                    title = "Error!",
                    messageType = Dialog.Message.Warning
                  )
                case Success(_) =>
                  print(s"[DEBUG] Moved to column $col\n")
              }
            }
          }
          cells(row)(col) = chip
          contents += chip
        }
      }
    }

    val mainGamePanel = new BorderPanel {
      layout(new FlowPanel {
        if (radio3.selected)
          contents += new Label("Current mode: 3 IN A ROW")
        else if (radio5.selected)
          contents += new Label("Current mode: 5 IN A ROW")
        else
          contents += new Label("Current mode: 4 IN A ROW")
        contents += Swing.HStrut(30)
        contents += timerLabel
        contents += Swing.HStrut(30)
        contents += turnLabel
        contents += Swing.HStrut(30)
        contents += undoButton
        contents += Swing.HStrut(5)
        contents += redoButton
      }) = BorderPanel.Position.North
      layout(gridPanel) = BorderPanel.Position.Center
    }

    contents = mainGamePanel
    this.pack()
    this.centerOnScreen()
    timer.start()
  }
  
  this.pack()
  this.centerOnScreen()

  override def update(): Unit = {
    val p1Symbol = controller.getPlayerSymbol(0)
    val p2Symbol = controller.getPlayerSymbol(1)
    val currentPlayer = controller.getPlayer
    turnLabel.text = s"${currentPlayer.name}'s move >>> "

    for (row <- 0 until 6) {
      for (col <- 0 until 7) {
        val cellState = controller.board.getCell(row, col)
        val button = cells(row)(col)

        cellState match {
          case " " =>
            button.background = java.awt.Color.WHITE
            button.borderPainted = true
          case s if s == p1Symbol =>
            button.background = java.awt.Color.RED
            button.focusPainted = false
            button.borderPainted = false
          case s if s == p2Symbol =>
            button.background = java.awt.Color.YELLOW
            button.focusPainted = false
            button.borderPainted = false
        }
      }
    }

    this.repaint()

    if (controller.isGameOver) {
      timer.stop()

      controller.winner match {
        case Some(player) =>
          Dialog.showMessage(
            parent = this,
            message = s"Congratulations! ${player.name} won the game!",
            title = "Game Over!",
            messageType = Dialog.Message.Info
          )
        case None =>
          Dialog.showMessage(
            parent = this,
            message = "It's a draw! Game over.",
            title = "Game Over!",
            messageType = Dialog.Message.Info
          )
      }
      this.dispose()
    }
  }