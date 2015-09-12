package com.piotrglazar.scala

import java.util.Random

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.server.Directives._
import akka.stream.{ActorMaterializer, Materializer}
import com.typesafe.config.{Config, ConfigFactory}
import spray.json.DefaultJsonProtocol

import scala.concurrent.{ExecutionContextExecutor, Future}

case class RandomResponse(i: Int)

case class AddRequest(a: Int, b: Int)

case class AddResponse(value: Int)

trait Protocols extends DefaultJsonProtocol {
  implicit val randomResponseFormat = jsonFormat1(RandomResponse.apply)
  implicit val addRequestFormat = jsonFormat2(AddRequest.apply)
  implicit val addResponseFormat = jsonFormat1(AddResponse.apply)
}

trait Service extends Protocols {

  implicit val system: ActorSystem
  implicit def executor: ExecutionContextExecutor
  implicit val materializer: Materializer

  def config: Config
  var logger: LoggingAdapter

  val random: Random = new Random(System.currentTimeMillis())

  val routes = {
    logRequestResult("my-microservice") {
      pathPrefix("number") {
        (get & path(IntNumber)) { boundary =>
          complete {
            RandomResponse(random.nextInt(boundary))
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

object MyMicroservice extends App with Service {

  override implicit val system: ActorSystem = ActorSystem()
  override implicit def executor: ExecutionContextExecutor = system.dispatcher
  override implicit val materializer: Materializer = ActorMaterializer()

  override def config: Config = ConfigFactory.load()
  override var logger: LoggingAdapter = Logging(system, getClass)

  Http().bindAndHandle(routes, config.getString("http.interface"), config.getInt("http.port"))
}
