package com.coiney.akka.mailer.providers

import com.coiney.akka.mailer.{Email, EmailException}


object MailerProvider {
  trait Mailer {
    @throws(classOf[EmailException])
    def sendEmail(email: Email): Unit
  }
}

trait MailerProvider {
  def getMailer: MailerProvider.Mailer
}
