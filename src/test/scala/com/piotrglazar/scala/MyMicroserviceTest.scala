package com.piotrglazar.scala

import akka.event.{LoggingAdapter, NoLogging}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.piotrglazar.scala.api.RandomResponse
import com.piotrglazar.scala.app.Service
import com.typesafe.config.Config
import akka.http.scaladsl.model.StatusCodes.OK
import akka.http.scaladsl.model.ContentTypes.`application/json`
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

import org.scalatest.{FlatSpec, Matchers}

class MyMicroserviceTest extends FlatSpec with Matchers with ScalatestRouteTest with Service {
  override def testConfigSource = "akka.loglevel = WARNING"
  override def config: Config = testConfig

  override val random: TestRandomNumberGenerator = new TestRandomNumberGenerator
  override var logger: LoggingAdapter = NoLogging

  "Service" should "return a random number" in {
    // given
    random.shouldReturn(42)

    // when & then
    Get("/number") ~> routes ~> check {
      status shouldBe OK
      contentType shouldBe `application/json`
      responseAs[RandomResponse] shouldBe RandomResponse(42)
    }
  }
}
