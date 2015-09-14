package com.piotrglazar.scala.api

sealed trait Response

case class RandomResponse(i: Int) extends Response

case class AddResponse(calculationResult: Int) extends Response

case class ExchangeRateApiResponse(disclaimer: String, license: String, timestamp: Long, base: String, rates: Map[String, BigDecimal]) extends Response

case class CurrenciesResponse(rates: Map[String, BigDecimal]) extends Response
