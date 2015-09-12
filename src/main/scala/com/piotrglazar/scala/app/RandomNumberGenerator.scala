package com.piotrglazar.scala.app

import java.util.Random

trait RandomNumberGenerator {
  
  def nextInt(): Int
  
  def nextInt(bound: Int): Int
}

class DefaultRandomNumberGenerator extends RandomNumberGenerator {
  
  val rng: Random = new Random(System.currentTimeMillis())
  
  override def nextInt(): Int = {
    rng.nextInt()
  }

  override def nextInt(bound: Int): Int = {
    rng.nextInt(bound)
  }
}