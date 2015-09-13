package com.piotrglazar.scala.app

import java.io.IOException

import akka.actor.ActorSystem
import akka.event.LoggingAdapter
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.{Uri, HttpResponse, HttpRequest}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.PathMatchers.IntNumber
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.Materializer
import akka.stream.scaladsl.{Sink, Source, Flow}
import com.piotrglazar.scala.api.{CurrenciesRequest, ExchangeRateApiResponse, AddRequest, AddResponse, Protocols, RandomResponse}
import com.typesafe.config.Config

import scala.concurrent.{Future, ExecutionContextExecutor}

trait Service extends Protocols {

  implicit val system: ActorSystem
  implicit def executor: ExecutionContextExecutor
  implicit val materializer: Materializer

  def config: Config
  var logger: LoggingAdapter

  val random: RandomNumberGenerator

  val currencies: ExchangeRatesFetcher = new ExchangeRatesFetcher

  val routes = {
    logRequestResult("my-microservice") {
      pathPrefix("number") {
        (get & path(IntNumber)) { bound =>
          complete {
            RandomResponse(random.nextInt(bound))
          }
        } ~
        (get & pathEnd) {
          complete {
            RandomResponse(random.nextInt())
          }
        } ~
        (post & entity(as[AddRequest])) { addRequest =>
          complete {
            AddResponse(addRequest.a + addRequest.b)
          }
        }
      } ~
      pathPrefix("finance") {
        (post & entity(as[CurrenciesRequest])) { currenciesRequest =>
          complete {
            fetchCurrencies().map[ToResponseMarshallable] {
              case Right(response) => currencies.getExchangeRates(response, currenciesRequest.currencies)
              case Left(error) => BadRequest -> error
            }
          }
        }
      }
    }
  }

  lazy val openExchangeRateConnectionFlow: Flow[HttpRequest, HttpResponse, Any] =
    Http().outgoingConnection(config.getString("openExchangeRate.url"))

  def openExchangeRateRequest(httpRequest: HttpRequest): Future[HttpResponse] =
    Source.single(httpRequest).via(openExchangeRateConnectionFlow).runWith(Sink.head)

  def openExchangeRateUri(): Uri = Uri(config.getString("openExchangeRate.endpoint"))
    .withQuery(Map(config.getString("openExchangeRate.appKey") -> config.getString("openExchangeRate.appValue")))

  def fetchCurrencies(): Future[Either[String, ExchangeRateApiResponse]] = {
    openExchangeRateRequest(RequestBuilding.Get(openExchangeRateUri())).flatMap { response =>
      response.status match {
        case OK => Unmarshal(response.entity).to[ExchangeRateApiResponse].map(Right(_))
        case _ => Unmarshal(response.entity).to[String].flatMap { entity =>
          val error = s"OpenExchangeRate request failed with status code ${response.status} and entity $entity"
          logger.error(error)
          Future.failed(new IOException(error))
        }
      }
    }
  }
}
