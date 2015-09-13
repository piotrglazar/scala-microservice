package com.piotrglazar.scala.app

import com.piotrglazar.scala.api.{CurrenciesResponse, ExchangeRateApiResponse}

class ExchangeRatesFetcher {

  def getExchangeRates(rates: ExchangeRateApiResponse, currencies: Set[String]): CurrenciesResponse = {
    CurrenciesResponse(rates.rates.filterKeys(currencies.contains))
  }
}
