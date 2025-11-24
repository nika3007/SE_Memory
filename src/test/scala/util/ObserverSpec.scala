package util

import java.security.KeyStore.TrustedCertificateEntry

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class ObservableSpec extends AnyWordSpec with Matchers {
  "An Observable" should {
    val observable = new Observable

    // Effekt-Tracker außerhalb des anonymen Observer
    var wasUpdated = false

    val observer = new Observer {
      override def update: Boolean = {
        wasUpdated = true
        true
      }
    }
    "add an Observer" in {
      observable.add(observer)
      observable.subscribers should contain (observer)
    }
    "notify an Observer" in {
      wasUpdated shouldBe false
      observable.notifyObservers
      wasUpdated shouldBe true
    }
    "remove an Observer" in {
      observable.remove(observer)
      observable.subscribers should not contain (observer)
    }

  }

}
