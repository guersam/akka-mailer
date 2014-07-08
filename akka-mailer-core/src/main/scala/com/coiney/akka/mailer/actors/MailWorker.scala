package com.coiney.akka.mailer.actors

import akka.actor.{ActorLogging, Actor, Props}

import scala.concurrent.duration.FiniteDuration

import com.coiney.akka.mailer.{MailConfig, Email}
import com.coiney.akka.mailer.util.Mailer
import com.coiney.akka.mailer.util.commons.CommonsMailer


private[mailer] object MailWorker {
  case class SendEmail(email: Email, attempts: Int, retryOn: FiniteDuration)

  def apply(mailConfig: MailConfig): MailWorker = new MailWorker(mailConfig) with MailDispatcher {
    override protected lazy val mailer: Mailer =
      CommonsMailer(
        mailConfig.host,
        mailConfig.port,
        mailConfig.username,
        mailConfig.password,
        mailConfig.tls,
        mailConfig.ssl,
        mailConfig.socketConnectionTimeout,
        mailConfig.socketTimeout
      )
  }

  def props(mailConfig: MailConfig): Props = Props(MailWorker(mailConfig))
}

private[mailer] class MailWorker(mailConfig: MailConfig) extends Actor
                                                         with ActorLogging {
  this: MailDispatcher =>
  import MailWorker._

  override def receive: Actor.Receive = {
    case SendEmail(email, attempts, retryOn) => ()
      mailer.sendEmail(email)
      context.stop(self)
  }

  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    val se = message.get.asInstanceOf[SendEmail]
    val newSe = se.copy(attempts = se.attempts + 1)
    log.debug(s"Scheduling ${newSe.email} resend for attempt nr. ${newSe.attempts}")
    context.system.scheduler.scheduleOnce(newSe.retryOn, self, newSe)(context.dispatcher)
  }

  override def postStop(): Unit = {}

}