package com.piotrglazar.scala

import akka.event.{LoggingAdapter, NoLogging}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.ContentTypes.`application/json`
import akka.http.scaladsl.model.StatusCodes.{InternalServerError, OK}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.piotrglazar.scala.api.{AddRequest, AddResponse, CurrenciesRequest, CurrenciesResponse, RandomResponse}
import com.piotrglazar.scala.app.{ExchangeRatesService, Service}
import com.typesafe.config.Config
import org.scalatest.{FlatSpec, Matchers}

class MyMicroserviceTest extends FlatSpec with Matchers with ScalatestRouteTest with Service {

  override def testConfigSource = "akka.loglevel = WARNING"
  override def config: Config = testConfig

  val exchangeRateStubServer = new ExchangeRatesStubServer(executor, materializer)

  override lazy val openExchangeRateConnectionFlow = exchangeRateStubServer.flow()

  override val exchangeRatesService: ExchangeRatesService = new ExchangeRatesService
  override val random: TestRandomNumberGenerator = new TestRandomNumberGenerator
  override var logger: LoggingAdapter = NoLogging

  "Service" should "return a random number" in {
    // given
    random.shouldReturn(42)

    // when
    val result = Get("/number") ~> routes

    // then
    result ~> check {
      status shouldBe OK
      contentType shouldBe `application/json`
      responseAs[RandomResponse] shouldBe RandomResponse(42)
    }
  }

  "Service" should "return a bounded number" in {
    // given
    random.shouldReturn(42)

    // when
    val result = Get("/number/50") ~> routes

    // then
    result ~> check {
      status shouldBe OK
      contentType shouldBe `application/json`
      responseAs[RandomResponse] shouldBe RandomResponse(42)
    }
  }

  "Service" should "add numbers for user" in {
    // given
    val request = AddRequest(3, 5)

    // when
    val result = Post("/number", request) ~> routes

    // then
    result ~> check {
      status shouldBe OK
      responseAs[AddResponse].calculationResult shouldBe 8
    }
  }

  "Service" should "fetch info from ExchangeRates" in {
    // given
    exchangeRateStubServer.withEntity("USD", Map("PLN" -> BigDecimal("3.0")))
    val request = CurrenciesRequest(Set("PLN"))

    // when
    val result = Post("/finance", request) ~> routes

    // then
    result ~> check {
      status shouldBe OK
      val response: CurrenciesResponse = responseAs[CurrenciesResponse]
      response.rates shouldBe Map("PLN" -> BigDecimal("3.0"))
      response.base shouldBe "USD"
    }
  }

  "Service" should "not break when there is no data for currency" in {
    // given
    exchangeRateStubServer.withEntity("EUR", Map("PLN" -> BigDecimal("3.0")))
    val request = CurrenciesRequest(Set("PLN", "XYZ"))

    // when
    val result = Post("/finance", request) ~> routes

    // then
    result ~> check {
      status shouldBe OK
      val response: CurrenciesResponse = responseAs[CurrenciesResponse]
      response.rates shouldBe Map("PLN" -> BigDecimal("3.0"))
      response.base shouldBe "EUR"
    }
  }

  "Service" should "not break when request to exchange rates fails" in {
    // given
    exchangeRateStubServer.withError(InternalServerError, "test exception")
    val request = CurrenciesRequest(Set("PLN"))

    // when
    val result = Post("/finance", request) ~> routes

    // then
    result ~> check {
      status shouldBe InternalServerError
      responseAs[String] shouldBe "There was an internal server error."
    }
  }
}
