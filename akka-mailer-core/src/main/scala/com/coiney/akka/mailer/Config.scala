package com.coiney.akka.mailer

import com.typesafe.config.Config

import scala.concurrent.duration._


case class MailConfig (
  host: String,
  port: Int,
  username: String,
  password: String,
  ssl: Boolean,
  tls: Boolean,
  socketConnectionTimeout: FiniteDuration,
  socketTimeout: FiniteDuration,
  maxNrOfRetries: Int,
  retryAfter: FiniteDuration
)

object MailConfig {
  def apply(config: Config): MailConfig = new MailConfig(
    host                    = config.getString("smtp.host"),
    port                    = config.getInt("smtp.port"),
    username                = config.getString("smtp.username"),
    password                = config.getString("smtp.password"),
    ssl                     = config.getBoolean("smtp.ssl"),
    tls                     = config.getBoolean("smtp.tls"),
    socketConnectionTimeout = config.getDuration("smtp.socket-connection-timeout", MILLISECONDS).millis,
    socketTimeout           = config.getDuration("smtp.socket-timeout", MILLISECONDS).millis,
    maxNrOfRetries          = config.getInt("max-nr-of-retries"),
    retryAfter              = config.getDuration("retry-after", MILLISECONDS).millis
  )
}