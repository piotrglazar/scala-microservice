package com.piotrglazar.scala

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, StatusCode}
import akka.http.scaladsl.testkit.MarshallingTestUtils
import akka.stream.Materializer
import akka.stream.scaladsl.Flow
import com.piotrglazar.scala.api.{Protocols, ExchangeRateApiResponse}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

import scala.concurrent.ExecutionContext

class ExchangeRatesStubServer(ec: ExecutionContext, mat: Materializer) extends MarshallingTestUtils with Protocols {

  implicit val executor: ExecutionContext = ec
  implicit val materializer: Materializer = mat

  val defaultResponse = HttpResponse(status = OK, entity = marshal(defaultResponseEntity()))

  var response = defaultResponse

  private def defaultResponseEntity(): ExchangeRateApiResponse = {
    ExchangeRateApiResponse("default", "default", 0, "USD", Map("PLN" -> BigDecimal("3.0")))
  }

  def withEntity(base: String, rates: Map[String, BigDecimal]): Unit = {
    response = HttpResponse(status = OK, entity = marshal(ExchangeRateApiResponse("test", "test", 0, base, rates)))
  }
  
  def withError(status: StatusCode, message: String): Unit = {
    response = HttpResponse(status = status, entity = marshal(message))
  }
  
  def flow(): Flow[HttpRequest, HttpResponse, Any] = {
    Flow[HttpRequest].map { request =>
      response
    }
  }
}
