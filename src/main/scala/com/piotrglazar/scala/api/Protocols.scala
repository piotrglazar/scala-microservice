package com.piotrglazar.scala.api

import spray.json.DefaultJsonProtocol

trait Protocols extends DefaultJsonProtocol {
  implicit val randomResponseFormat = jsonFormat1(RandomResponse.apply)
  implicit val addRequestFormat = jsonFormat2(AddRequest.apply)
  implicit val addResponseFormat = jsonFormat1(AddResponse.apply)
}
