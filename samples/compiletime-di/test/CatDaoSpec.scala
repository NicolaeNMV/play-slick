package test

import dao.CatDAO
import models.Cat
import org.specs2.mutable.Specification
import test.TestEnvironment.WithApplicationComponents

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}
import core.ApplicationComponents
import TestEnvironment._
import org.specs2.mock.Mockito

/** test the kitty cat database */
class CatDAOSpec extends Specification with Mockito {

  "CatDAO" should {
    val app = initAppCustomComponents { context =>
      new ApplicationComponents(context) {
        override lazy val catDao: CatDAO = mock[CatDAO]
      }
    }
    "work as expected" in {

      val testKitties = Set(
        Cat("kit", "black"),
        Cat("garfield", "orange"),
        Cat("creme puff", "grey"))

      val dao: CatDAO = app.catDao
      dao.insert(any[Cat]) returns Future.successful(1)
      dao.all returns Future.successful(testKitties.toSeq)

      Await.result(Future.sequence(testKitties.map(dao.insert)), 1 seconds)
      val storedCats = Await.result(dao.all(), 1 seconds)

      storedCats.toSet must equalTo(testKitties)
    }
  }
}
