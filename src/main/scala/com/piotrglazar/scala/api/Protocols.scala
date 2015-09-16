package com.piotrglazar.scala.api

import spray.json.DefaultJsonProtocol

trait Protocols extends DefaultJsonProtocol {
  implicit val addRequestFormat = jsonFormat2(AddRequest.apply)
  implicit val exchangeRateRequestFormat = jsonFormat1(CurrenciesRequest.apply)

  implicit val randomResponseFormat = jsonFormat1(RandomResponse.apply)
  implicit val addResponseFormat = jsonFormat1(AddResponse.apply)
  implicit val exchangeRateResponseFormat = jsonFormat5(ExchangeRateApiResponse.apply)
  implicit val currenciesResponse = jsonFormat2(CurrenciesResponse.apply)
}
