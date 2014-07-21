package com.coiney.akka.mailer.providers

import com.coiney.akka.mailer.EmailException
import com.coiney.akka.mailer.protocol.Email


object MailerProvider {
  trait Mailer {
    @throws(classOf[EmailException])
    def sendEmail(email: Email): Unit
  }
}

trait MailerProvider {
  def getMailer: MailerProvider.Mailer
}
