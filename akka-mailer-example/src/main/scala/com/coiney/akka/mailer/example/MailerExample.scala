package com.coiney.akka.mailer.example

import akka.actor.ActorSystem
import com.coiney.akka.mailer.{Correspondent, Email, MailerSystem}






object MailerTest extends App {

  implicit val system = ActorSystem("mailer-system")

  val mailerSystem = MailerSystem()

  val service = mailerSystem.createService()

  for(i <- 1 to 10) { mailerSystem.createDispatcher(service) }

  // Send an email
  service ! Email("Haiku", Correspondent("john@doe.com", Some("John Doe")), to = List(Correspondent("pjan@coiney.com", Some("Foo Bar"))), html = Some("<p>an aging willow--<br />its image unsteady<br />in the flowing stream</p>"))
  service ! Email("Haiku", Correspondent("john@doe.com", Some("John Doe")), to = List(Correspondent("pjan@coiney.com", Some("Foo Bar"))), html = Some("<p>an aging willow--<br />its image unsteady<br />in the flowing stream</p>"))
  service ! Email("Haiku", Correspondent("john@doe.com", Some("John Doe")), to = List(Correspondent("pjan@coiney.com", Some("Foo Bar"))), html = Some("<p>an aging willow--<br />its image unsteady<br />in the flowing stream</p>"))

  // Shutdown the system
  Thread.sleep(10000)
  system.shutdown()

}
