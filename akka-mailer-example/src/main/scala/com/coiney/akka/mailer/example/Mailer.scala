package com.coiney.akka.mailer.example

import akka.actor.ActorSystem
import com.coiney.akka.mailer.{MailConfig, Correspondent, Email}
import com.coiney.akka.mailer.actors.MailService
import com.typesafe.config.ConfigFactory

object Mailer extends App {

  implicit val system = ActorSystem("mailer")

  // load the configuration
  val cfg = ConfigFactory.load()
  val mailConfig = MailConfig(cfg.getConfig("mailer"))

  // create a MailService Actor
  val mailer = system.actorOf(MailService.props(mailConfig))

  // Send an email
  mailer ! Email("Haiku", Correspondent("john@doe.com", Some("John Doe")), to = List(Correspondent("foo@bar.com", Some("Foo Bar"))), html = Some("<p>an aging willow--<br />its image unsteady<br />in the flowing stream</p>"))

  // Shutdown the system
  Thread.sleep(60000)
  system.shutdown()

}
