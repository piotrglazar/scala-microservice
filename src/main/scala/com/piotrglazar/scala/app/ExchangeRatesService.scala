package com.piotrglazar.scala.app

import com.piotrglazar.scala.api.{CurrenciesResponse, ExchangeRateApiResponse}

class ExchangeRatesService {

  def getExchangeRates(rates: ExchangeRateApiResponse, currencies: Set[String]): CurrenciesResponse = {
    CurrenciesResponse(rates.base, rates.rates.filterKeys(currencies.contains))
  }
}
