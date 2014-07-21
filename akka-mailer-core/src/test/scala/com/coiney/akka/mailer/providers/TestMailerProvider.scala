package com.coiney.akka.mailer.providers

import com.coiney.akka.mailer.{MailerSystem, EmailException}
import com.coiney.akka.mailer.protocol.Email
import com.coiney.akka.mailer.providers.MailerProvider.Mailer

class TestMailerProvider(settings: MailerSystem.Settings) extends MailerProvider {
  override def getMailer: Mailer = new TestMailer

  class TestMailer extends Mailer {
    @throws(classOf[EmailException])
    override def sendEmail(email: Email): Unit = {}
  }
}
