package com.coiney.akka.mailer.actors

import com.coiney.akka.mailer.util.Mailer


trait MailDispatcher {
  protected def mailer: Mailer
}