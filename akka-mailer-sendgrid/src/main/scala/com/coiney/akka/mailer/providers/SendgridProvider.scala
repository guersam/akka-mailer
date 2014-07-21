package com.coiney.akka.mailer.providers

import java.net.URLEncoder

import com.coiney.akka.mailer.EmailException
import com.coiney.akka.mailer.protocol.Email
import com.coiney.akka.mailer.MailerSystem.Settings
import com.typesafe.config.{ConfigFactory, Config}
import scalaj.http.{Http, HttpOptions}


class SendgridProvider(settings: Settings) extends MailerProvider {
  import MailerProvider._

  override def getMailer: Mailer = new SendgridMailer(settings)

  class SendgridMailer(settings: Settings) extends Mailer {

    final val config: Config = {
      val config = settings.config
      config.checkValid(ConfigFactory.defaultReference(), "mailer.sendgrid")
      config
    }

    import config._

    final private val username = getString("mailer.sendgrid.user")
    final private val password = getString("mailer.sendgrid.key")

    final val apiUrl = "https://api.sendgrid.com"
    final val apiEndpoint = "/api/mail.send.json"

    def postDataMap(email: Email): Map[String, String] = {
      var postData = Map.empty[String, String]

      postData += ("api_user" -> username)
      postData += ("api_key" -> password)

      // add subject
      postData += ("subject" -> email.subject)

      // add from
      postData += ("from" -> email.from.email)
      email.from.name.foreach{ name => postData += ("fromname" -> name) }

      // add to
      for(i <- 0 until email.to.length) {
        postData += (s"to[$i]" -> email.to(i).email)
        email.to(i).name.foreach{ name => postData += (s"toname[$i]" -> name) }
      }

      // add cc
      for(i <- 0 until email.cc.length) {
        postData += (s"cc[$i]" -> email.cc(i).email)
        email.cc(i).name.foreach{ name => postData += (s"ccname[$i]" -> name) }
      }

      // add bcc
      for(i <- 0 until email.bcc.length) {
        postData += (s"bcc[$i]" -> email.bcc(i).email)
        email.bcc(i).name.foreach{ name => postData += (s"bccname[$i]" -> name) }
      }

      // add html
      email.html.foreach{ html => postData += ("html" -> html) }

      // add text
      email.text.foreach{ text => postData += ("text" -> text) }

      // add replyTo
      email.replyTo.foreach{ replyTo => postData += ("replyto" -> replyTo.email) }

      // add Headers
      // To Be Done

      postData
    }

    def urlEncode(postData: Map[String, String]): String =
      postData.foldRight(""){ case ((key, value), acc) => s"$key=${URLEncoder.encode(value, "UTF-8")}&$acc"}

    @throws(classOf[EmailException])
    override def sendEmail(email: Email): Unit = {
      val responseCode = Http
        .postData(
          s"$apiUrl$apiEndpoint",
          urlEncode(postDataMap(email)))
        .options(
          HttpOptions.connTimeout(1000),
          HttpOptions.readTimeout(5000))
        .responseCode
      if (responseCode != 200) throw new EmailException(s"Sendgrid API Server returned HTTP response code $responseCode")
    }
  }
}
