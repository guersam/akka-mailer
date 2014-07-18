package com.coiney.akka.mailer.actors

import akka.actor._
import akka.actor.SupervisorStrategy.Restart

import com.coiney.akka.mailer.MailerSystem


object Service {

  object DispatcherSupervisor {
    def apply(master: ActorRef, settings: MailerSystem.Settings): DispatcherSupervisor =
      new DispatcherSupervisor(master, settings)
    def props(master: ActorRef, settings: MailerSystem.Settings): Props =
      Props(DispatcherSupervisor(master, settings))
  }
  class DispatcherSupervisor(master: ActorRef, settings: MailerSystem.Settings) extends Actor
                                                                                with ActorLogging {

    for (i <- 1 to settings.nrOfDispatchers) {
      context.actorOf(Dispatcher.props(master, settings), "dispatcher-%03d".format(i))
    }

    override def receive: Actor.Receive = {
      case msg => log.error("DispatcherSupervisor received a message while it should never.")
    }

    override def supervisorStrategy = OneForOneStrategy(){
      case _ => Restart
    }
  }


  def apply(settings: MailerSystem.Settings): Service =
    new Service(settings)
  def props(settings: MailerSystem.Settings): Props =
    Props(Service(settings))
}

class Service(settings: MailerSystem.Settings) extends Actor {
  import Service._

  val master = context.actorOf(Master.props(settings), s"master")

  val dispatchers = context.actorOf(DispatcherSupervisor.props(master, settings), s"dispatchers")

  override def receive: Actor.Receive = {
    case msg =>
      master forward msg
  }

  override def supervisorStrategy = AllForOneStrategy(){
    case _ => Restart
  }

}
