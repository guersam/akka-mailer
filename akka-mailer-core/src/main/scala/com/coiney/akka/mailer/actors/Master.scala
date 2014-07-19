package com.coiney.akka.mailer.actors

import akka.actor._

import com.coiney.akka.mailer.{MailerSystem, Email}

import scala.collection.immutable.Queue
import scala.concurrent.duration._


object Master {
  case class DispatcherCreated(dispatcher: ActorRef)
  case class MailRequest(dispatcher: ActorRef)
  case class MailSent(dispatcher: ActorRef)
  case class MailFailed(dispatcher: ActorRef)

  // intra-actor class
  case class ScheduledEmail(email: Email, attempt: Int)

  def apply(settings: MailerSystem.Settings): Master =
    new Master(settings)
  def props(settings: MailerSystem.Settings): Props =
    Props(Master(settings))
}

class Master(settings: MailerSystem.Settings) extends Actor
                                              with ActorLogging {
  import Master._
  import settings._

  implicit val ec = context.dispatcher

  var dispatchers = Map.empty[ActorRef, Option[(ActorRef, ScheduledEmail)]]
  // Holds the list of incoming emails, as wel as a reference to it's requester
  var emailQueue = Queue.empty[(ActorRef, ScheduledEmail)]

  override def receive: Actor.Receive = {
    case DispatcherCreated(dispatcher) =>
      context.watch(dispatcher)
      dispatchers += (dispatcher -> None)
      notifyDispatchers()

    case MailRequest(dispatcher) =>
      if (dispatchers.contains(dispatcher)) {
        if (emailQueue.isEmpty) dispatcher ! Dispatcher.NoMails
        else if (dispatchers(dispatcher) == None) {
          val ((requester, scheduledEmail @ ScheduledEmail(email, _)), newEmailQueue) = emailQueue.dequeue
          emailQueue = newEmailQueue
          dispatchers += (dispatcher -> Some((requester, scheduledEmail)))
          dispatcher.tell(Dispatcher.SendMail(email), requester)
        }
      }

    case MailSent(dispatcher) =>
      if (!dispatchers.contains(dispatcher))
        log.debug(s"Received MailSent from an unknown dispatcher [$dispatcher]")
      else
        dispatchers += (dispatcher -> None)

    case MailFailed(dispatcher) =>
      if (!dispatchers.contains(dispatcher)) {
        log.debug(s"Received MailFailed from an unknown dispatcher [$dispatcher]")
      } else if (dispatchers(dispatcher) != None) {
        recoverFromFailure(dispatcher)
        dispatchers += (dispatcher -> None)
      }

    case Terminated(dispatcher) =>
      if (dispatchers.contains(dispatcher) && dispatchers(dispatcher) != None) {
        recoverFromFailure(dispatcher)
        dispatchers -= dispatcher
      }

    case email: Email =>
      emailQueue = emailQueue.enqueue(sender -> ScheduledEmail(email, 1))
      notifyDispatchers()

    case scheduledEmail @ ScheduledEmail(email, attempt) =>
      emailQueue = emailQueue.enqueue(sender -> scheduledEmail)
      notifyDispatchers()
  }

  private def recoverFromFailure(dispatcher: ActorRef): Unit = {
    val (requester, ScheduledEmail(email, attempt)) = dispatchers(dispatcher).get
    log.debug(s"Dispatcher [$dispatcher] died while processing $email for $requester on attempt nr. $attempt.")
    if (attempt < maxNrOfRetries)
      scheduleRetry(retryAfter, ScheduledEmail(email, attempt + 1), requester)
    else
      log.error(s"Sending $email failed after $attempt attempts.")
  }

  private def scheduleRetry(retryAfter: FiniteDuration, scheduledEmail: ScheduledEmail, requester: ActorRef): Unit = {
    context.system.scheduler.scheduleOnce(retryAfter)(self.tell(scheduledEmail, requester))
  }

  private def notifyDispatchers(): Unit = {
    if (emailQueue.nonEmpty) {
      dispatchers.foreach {
        case (worker, None) => worker ! Dispatcher.MailsAvailable
        case _ =>
      }
    }
  }

}

