package com.coiney.akka.mailer.actors

import akka.actor._
import akka.actor.SupervisorStrategy.{Escalate, Stop}

import com.coiney.akka.mailer.{EmailException, MailerSystem, Email}


object Dispatcher {
  case object MailsAvailable
  case object NoMails
  case class SendMail(email: Email)
  case class ProcessingFinished(result: Any)

  def apply(master: ActorRef, settings: MailerSystem.Settings): Dispatcher =
    new Dispatcher(master, settings)
  def props(master: ActorRef, settings: MailerSystem.Settings): Props =
    Props(Dispatcher(master, settings))
}

class Dispatcher(master: ActorRef, settings: MailerSystem.Settings) extends Actor
                                                                    with ActorLogging {
  import Dispatcher._
  import settings._

  override def preStart(): Unit = {
    master ! Master.DispatcherCreated(self)
  }

  // The dispatcher starts in idle state
  override def receive: Actor.Receive = idle

  def idle: Actor.Receive = {
    case NoMails =>
    // We asked for an Email, but maybe another actor got it first,
    // or there's literally no message to be processed. Stay Idle
    case MailsAvailable =>
      master ! Master.MailRequest(self)
    case SendMail(email) =>
      processEmail(requester = sender)(email)
      context.become(processing(email))
  }

  def processing(email: Email): Actor.Receive = {
    case MailsAvailable =>
    // When processing, we don't react on the availability of mails
    case NoMails =>
    // When processing, we shouldn't even get this message
    case SendMail(voidEmail) =>
      log.error(s"Dispatcher received an email [$voidEmail] while processing another one [$email].")
    case ProcessingFinished(result) =>
      master ! Master.MailSent(self)
      master ! Master.MailRequest(self)
      context.become(idle)
  }

  private def processEmail(requester: ActorRef)(email: Email): Unit = {
    val processor = context.actorOf(Processor.props(settings))
    processor.tell(email, requester)
  }

  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 0) {
      case _: EmailException =>
        master ! Master.MailFailed(self)
        master ! Master.MailRequest(self)
        context.become(idle)
        Stop
      case _ =>
        Escalate
    }

}