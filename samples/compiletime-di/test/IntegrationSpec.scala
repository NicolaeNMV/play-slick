package test

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._
import org.specs2.mock.Mockito

import TestEnvironment.initAppCustomComponents
import core.ApplicationComponents

import models.Cat
import dao.CatDAO
import scala.concurrent.Future

/**
 * add your integration spec here.
 * An integration test will fire up a whole play application in a real (or headless) browser
 */
class IntegrationSpec extends Specification with Mockito {

  import TestEnvironment.initAppComponents

  "Application" should {

    "work from within a browser" in {

      val componets = initAppCustomComponents { context =>
        new ApplicationComponents(context) {
          override lazy val catDao: CatDAO = mock[CatDAO]
        }
      }

      val testKitties = Set(
        Cat("kit", "black"),
        Cat("garfield", "orange"),
        Cat("creme puff", "grey"))

      val dao: CatDAO = componets.catDao
      dao.insert(any[Cat]) returns Future.successful(1)
      dao.all returns Future.successful(testKitties.toSeq)

      val port = 3333
      running(TestServer(port, componets.application), HTMLUNIT) { browser =>

        browser.goTo("http://localhost:"+port)
        browser.pageSource must contain("garfield")

      }
    }

  }

}
