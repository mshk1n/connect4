package util
import controller.Observer

trait Observable:
  private var subscribers: Vector[Observer] = Vector()

  def add(s: Observer): Unit =
    subscribers = subscribers :+ s

  def remove(s: Observer): Unit =
    subscribers = subscribers.filterNot(o => o == s)

  def notifyObservers(): Unit = 
    subscribers.foreach(_.update())