package com.piotrglazar.scala.api

sealed trait Request

case class AddRequest(a: Int, b: Int) extends Request

case class CurrenciesRequest(currencies: Set[String]) extends Request
