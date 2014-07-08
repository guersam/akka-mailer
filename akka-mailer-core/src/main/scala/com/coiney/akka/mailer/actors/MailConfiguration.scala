package com.coiney.akka.mailer.actors

import com.coiney.akka.mailer.MailConfig


trait MailConfiguration {
  protected def mailConfig: MailConfig
}