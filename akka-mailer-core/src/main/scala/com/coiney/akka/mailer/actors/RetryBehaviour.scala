package com.coiney.akka.mailer.actors

import scala.concurrent.duration.FiniteDuration


trait RetryBehaviour {
  def maxNrOfRetries: Int
  def retryAfter: FiniteDuration
}

trait ConfigRetryBehaviour extends RetryBehaviour {
  this: MailConfiguration =>
  override lazy val maxNrOfRetries: Int = mailConfig.maxNrOfRetries
  override lazy val retryAfter: FiniteDuration = mailConfig.retryAfter
}