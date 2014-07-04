package com.coiney.akka.mailer.actors

import com.typesafe.config.Config

import scala.concurrent.duration._


trait MailConfiguration {
  protected def mailConfig: MailConfig
}

trait MailConfig {
  def host: String
  def port: Int
  def username: String
  def password: String
  def ssl: Boolean
  def tls: Boolean
  def socketConnectionTimeout: FiniteDuration
  def socketTimeout: FiniteDuration
  def maxNrOfRetries: Int
  def retryAfter: FiniteDuration
}

object MailConfig {
  def apply(config: Config): MailConfig = new MailConfig {
    override def host: String                            = config.getString("mailer.smtp.host")
    override def port: Int                               = config.getInt("mailer.smtp.port")
    override def username: String                        = config.getString("mailer.smtp.username")
    override def password: String                        = config.getString("mailer.smtp.password")
    override def ssl: Boolean                            = config.getBoolean("mailer.smtp.ssl")
    override def tls: Boolean                            = config.getBoolean("mailer.smtp.tls")
    override def socketConnectionTimeout: FiniteDuration = config.getDuration("mailer.smtp.socket-connection-timeout", MILLISECONDS).millis
    override def socketTimeout: FiniteDuration           = config.getDuration("mailer.smtp.socket-timeout", MILLISECONDS).millis
    override def maxNrOfRetries: Int                     = config.getInt("mailer.max-nr-of-retries")
    override def retryAfter: FiniteDuration              = config.getDuration("mailer.retry-after", MILLISECONDS).millis
  }
}