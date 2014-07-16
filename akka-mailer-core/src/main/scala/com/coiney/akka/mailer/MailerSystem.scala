package com.coiney.akka.mailer

import akka.actor.{ActorRef, ActorRefFactory}
import com.typesafe.config.{Config, ConfigFactory}

import com.coiney.akka.mailer.actors.{Dispatcher, Master}

import scala.concurrent.duration._


object MailerSystem {

  val Version: String = "0.2.0-SNAPSHOT"

  def apply()(implicit _actorRefFactory: ActorRefFactory): MailerSystem =
    apply(None, None)(_actorRefFactory)

  def apply(config: Config)(implicit _actorRefFactory: ActorRefFactory): MailerSystem =
    apply(Option(config), None)(_actorRefFactory)

  def apply(config: Config, classLoader: ClassLoader)(implicit _actorRefFactory: ActorRefFactory): MailerSystem =
    apply(Option(config), Option(classLoader))(_actorRefFactory)

  def apply(config: Option[Config], classLoader: Option[ClassLoader])(implicit _actorRefFactory: ActorRefFactory): MailerSystem = {
    val cl = classLoader.getOrElse(findClassLoader())
    val mailerConfig = config.getOrElse(ConfigFactory.load(cl))
    new MailerSystem(mailerConfig, cl)(_actorRefFactory)
  }

  class Settings(classLoader: ClassLoader, cfg: Config) {

    /**
     * The Config backing this MailerSystem's Settings
     */
    final val config: Config = {
      val config = cfg.withFallback(ConfigFactory.load(classLoader))
      config.checkValid(ConfigFactory.defaultReference(classLoader), "mailer")
      config
    }

    import config._

    final val mailerProviderClass: String = getString("mailer.provider")
    final val maxNrOfRetries: Int = getInt("mailer.max-nr-of-retries")
    final val retryAfter: FiniteDuration = getDuration("mailer.retry-after", MILLISECONDS).millis

    /**
     * Returns the String representation of the Config that this Settings is backed by
     */
    override def toString: String = config.root.render
  }

  private[mailer] def findClassLoader(): ClassLoader = {
    Option(Thread.currentThread.getContextClassLoader) getOrElse
      getClass.getClassLoader
  }
}

class MailerSystem(mailerConfig: Config, classLoader: ClassLoader)(implicit _actorRefFactory: ActorRefFactory) {

  import MailerSystem._

  /**
   * The core settings extracted from the supplied configuration.
   */
  final val settings: Settings = new Settings(classLoader, mailerConfig)

  private val actorRefFactory = _actorRefFactory

  def createMaster(): ActorRef = actorRefFactory.actorOf(Master.props(settings))
  def createMaster(name: String): ActorRef = actorRefFactory.actorOf(Master.props(settings), name)

  def createDispatcher(master: ActorRef): ActorRef = actorRefFactory.actorOf(Dispatcher.props(master, settings))
  def createDispatcher(master: ActorRef, name: String): ActorRef = actorRefFactory.actorOf(Dispatcher.props(master, settings))

}