package util

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import controller.Observer

class ObservableSpec extends AnyWordSpec with Matchers {
  "An Observable" should {
    "add and notify observers" in {
      val observable = new Object with Observable
      var updated = false
      val observer = new Observer {
        def update(): Unit = updated = true
      }
      
      observable.add(observer)
      observable.notifyObservers()
      updated should be(true)
    }

    "remove observers" in {
      val observable = new Object with Observable
      var count = 0
      val observer = new Observer {
        def update(): Unit = count += 1
      }
      
      observable.add(observer)
      observable.remove(observer)
      observable.notifyObservers()
      count should be(0)
    }
  }
}