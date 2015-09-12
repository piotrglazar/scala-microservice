package com.piotrglazar.scala.app

import akka.actor.ActorSystem
import akka.event.LoggingAdapter
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.PathMatchers.IntNumber
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.stream.Materializer
import com.piotrglazar.scala.api.{AddRequest, AddResponse, Protocols, RandomResponse}
import com.typesafe.config.Config

import scala.concurrent.ExecutionContextExecutor

trait Service extends Protocols {

  implicit val system: ActorSystem
  implicit def executor: ExecutionContextExecutor
  implicit val materializer: Materializer

  def config: Config
  var logger: LoggingAdapter

  val random: RandomNumberGenerator

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
      }
    }
  }
}
