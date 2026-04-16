case class Player(name: String, symbol: String, color: ConsoleColors):
  def coloredSymbol: String = color(symbol)