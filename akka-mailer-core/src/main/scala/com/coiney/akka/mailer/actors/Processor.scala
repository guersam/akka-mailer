package com.coiney.akka.mailer.actors

import akka.actor.{Props, Actor, ActorLogging}

import com.coiney.akka.mailer.{MailerSystem, Email}
import com.coiney.akka.mailer.providers.MailerProvider
import com.coiney.akka.mailer.util.{ReflectiveDynamicAccess, DynamicAccess}


object Processor {
  def apply(settings: MailerSystem.Settings): Processor =
    new Processor(settings)
  def props(settings: MailerSystem.Settings): Props =
    Props(Processor(settings))
}

class Processor(settings: MailerSystem.Settings) extends Actor
                                                 with ActorLogging {
  import settings._

  def classLoader: ClassLoader = getClass.getClassLoader

  protected def createDynamicAccess() = new ReflectiveDynamicAccess(classLoader)

  private val _dynamicAccess: DynamicAccess = createDynamicAccess()
  def dynamicAccess: DynamicAccess = _dynamicAccess

  val mailerProvider: MailerProvider = try {
    val args = Vector(
      classOf[MailerSystem.Settings] -> settings
    )
    dynamicAccess.createInstanceFor[MailerProvider](mailerProviderClass, args).get
  }

  val mailer: MailerProvider.Mailer = mailerProvider.getMailer

  override def receive: Actor.Receive = {
    case email: Email =>
      mailer.sendEmail(email)
      context.parent ! Dispatcher.ProcessingFinished(())
      context.stop(self)
  }
}

