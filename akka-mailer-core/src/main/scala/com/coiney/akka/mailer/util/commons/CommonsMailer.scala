package com.coiney.akka.mailer.util.commons

import org.apache.commons.mail.{EmailException => CEmailException, DefaultAuthenticator, HtmlEmail}

import scala.concurrent.duration._

import com.coiney.akka.mailer.util.Mailer
import com.coiney.akka.mailer.{Email, EmailException}


trait CommonsMailer extends Mailer {
  import Implicits._

  protected def host: String
  protected def port: Int
  protected def username: String
  protected def password: String
  protected def tls: Boolean
  protected def ssl: Boolean
  protected def socketConnectionTimeout: FiniteDuration
  protected def socketTimeout: FiniteDuration

  @throws(classOf[EmailException])
  override def sendEmail(email: Email): Unit = try {
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

object CommonsMailer {
  def apply(_host: String,
            _port: Int,
            _username: String,
            _password: String,
            _tls: Boolean = false,
            _ssl: Boolean = false,
            _socketConnectionTimeout: FiniteDuration = 60.seconds,
            _socketTimeout: FiniteDuration = 60.seconds): Mailer =
    new CommonsMailer {
      override protected val host: String = _host
      override protected val port: Int = _port
      override protected val username: String = _username
      override protected val password: String = _password
      override protected val tls: Boolean = _tls
      override protected val ssl: Boolean = _ssl
      override protected val socketConnectionTimeout: FiniteDuration = _socketConnectionTimeout
      override protected val socketTimeout: FiniteDuration = _socketTimeout
    }
}