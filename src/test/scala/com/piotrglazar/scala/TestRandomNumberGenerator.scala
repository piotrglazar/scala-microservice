package com.piotrglazar.scala

import com.piotrglazar.scala.app.RandomNumberGenerator

class TestRandomNumberGenerator extends RandomNumberGenerator {

  var n: Int = 0

  def shouldReturn(i: Int): Unit = {
    n = i
  }

  override def nextInt() = n

  override def nextInt(bound: Int): Int = Math.min(bound, n)
}
