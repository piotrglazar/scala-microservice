package com.piotrglazar.scala

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.Http
import akka.stream.{ActorMaterializer, Materializer}
import com.piotrglazar.scala.app.{DefaultRandomNumberGenerator, Service}
import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.ExecutionContextExecutor

object MyMicroservice extends App with Service {

  override implicit val system: ActorSystem = ActorSystem()
  override implicit def executor: ExecutionContextExecutor = system.dispatcher
  override implicit val materializer: Materializer = ActorMaterializer()

  override def config: Config = ConfigFactory.load()
  override var logger: LoggingAdapter = Logging(system, getClass)

  override val random = new DefaultRandomNumberGenerator

  Http().bindAndHandle(routes, config.getString("http.interface"), config.getInt("http.port"))
}
