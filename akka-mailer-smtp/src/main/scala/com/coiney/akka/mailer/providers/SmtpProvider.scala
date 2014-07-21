package com.coiney.akka.mailer.providers

import com.typesafe.config.{Config, ConfigFactory}
import org.apache.commons.mail.{DefaultAuthenticator, EmailException => CEmailException, HtmlEmail}

import com.coiney.akka.mailer.EmailException
import com.coiney.akka.mailer.protocol.Email
import com.coiney.akka.mailer.MailerSystem.Settings

import scala.concurrent.duration._


class SmtpProvider(settings: Settings) extends MailerProvider {
  import MailerProvider._

  final val config: Config = {
    val config = settings.config
    config.checkValid(ConfigFactory.defaultReference(), "mailer.smtp")
    config
  }

  import config._

  final private val host: String                            = getString("mailer.smtp.host")
  final private val port: Int                               = getInt("mailer.smtp.port")
  final private val username: String                        = getString("mailer.smtp.username")
  final private val password: String                        = getString("mailer.smtp.password")
  final private val tls: Boolean                            = getBoolean("mailer.smtp.tls")
  final private val ssl: Boolean                            = getBoolean("mailer.smtp.ssl")
  final private val socketConnectionTimeout: FiniteDuration = getDuration("mailer.smtp.socket-connection-timeout", MILLISECONDS).millis
  final private val socketTimeout: FiniteDuration           = getDuration("mailer.smtp.socket-timeout", MILLISECONDS).millis

  override def getMailer: Mailer = new SmtpMailer(settings)


  class SmtpMailer(settings: Settings) extends Mailer {
    import HtmlEmailImplicits._

    @throws(classOf[EmailException])
    override def sendEmail(email: Email): Unit = {
      try {
        val mail = new HtmlEmail()
        mail.setHostName(host)
        mail.setSmtpPort(port)
        mail.setAuthenticator(new DefaultAuthenticator(
          username,
          password
        ))
        mail.setStartTLSEnabled(tls)
        mail.setSSLOnConnect(ssl)
        mail.setSocketConnectionTimeout(socketConnectionTimeout.toMillis.toInt)
        mail.setSocketTimeout(socketTimeout.toMillis.toInt)
        mail.composeEmail(email)
        mail.send()
      } catch {
        case ee: CEmailException => throw new EmailException(ee.getMessage, ee.getCause)
      }
    }
  }
}
