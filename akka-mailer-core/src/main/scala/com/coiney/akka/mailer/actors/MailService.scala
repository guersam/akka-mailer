package com.coiney.akka.mailer.actors

import akka.actor.SupervisorStrategy.{Restart, Stop}
import akka.actor.{Actor, ActorLogging, OneForOneStrategy, Props}
import com.typesafe.config.Config

import com.coiney.akka.mailer.{EmailException, Email}


object MailService {
  def apply(cfg: Config): MailService = new MailService with MailConfiguration with ConfigRetryBehaviour {
    override protected lazy val mailConfig: MailConfig = MailConfig(cfg)
  }
  def props(cfg: Config): Props = Props(MailService(cfg))
}

class MailService extends Actor
                  with ActorLogging {
  this: MailConfiguration with RetryBehaviour =>

  override def receive: Actor.Receive = {
    case e: Email =>
      val mailWorker = context.actorOf(MailWorker.props(mailConfig))
      mailWorker ! MailWorker.SendEmail(e, 1, retryAfter)
  }

  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = maxNrOfRetries) {
      case ex: EmailException =>
        log.debug(s"Resend after EmailException ${ex.getCause}")
        Restart
      // No other exceptions are supposed to happen, so if they do, there's no way to recover.
      case _ =>
        Stop
    }

}