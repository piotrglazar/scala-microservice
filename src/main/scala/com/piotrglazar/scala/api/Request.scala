package com.piotrglazar.scala.api

sealed trait Request

case class RandomResponse(i: Int) extends Request

case class AddRequest(a: Int, b: Int) extends Request

case class AddResponse(value: Int) extends Request

case class ExchangeRateApiResponse(disclaimer: String, license: String, timestamp: Long, base: String, rates: Map[String, BigDecimal]) extends Request

case class CurrenciesRequest(currencies: Set[String]) extends Request

case class CurrenciesResponse(rates: Map[String, BigDecimal]) extends Request
