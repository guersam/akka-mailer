package com.coiney.akka.mailer.example

import com.coiney.akka.mailer.{EmailException, MailerSystem}
import com.coiney.akka.mailer.protocol.Email
import com.coiney.akka.mailer.providers.MailerProvider


class ExampleMailerProvider(settings: MailerSystem.Settings) extends MailerProvider {
  import MailerProvider._

  class DummyMailer extends Mailer {
    @throws(classOf[EmailException])
    override def sendEmail(email: Email): Unit = println(email)
  }

  override def getMailer: Mailer = new DummyMailer

}
