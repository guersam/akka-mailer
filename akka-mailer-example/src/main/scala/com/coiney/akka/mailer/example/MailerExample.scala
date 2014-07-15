package com.coiney.akka.mailer.example

import akka.actor.ActorSystem

import com.coiney.akka.mailer.{Correspondent, Email, MailerSystem}


object MailerExample extends App {

  implicit val system = ActorSystem("mailer-system")

  val mailerSystem = MailerSystem()

  val service = mailerSystem.createService()

  for(i <- 1 to 3) { mailerSystem.createDispatcher(service) }

  // Send an email
  service ! Email("Haiku-1", Correspondent("john@doe.com", Some("John Doe")), to = List(Correspondent("foo@bar.com", Some("Foo Bar"))), html = Some("<p>an aging willow--<br />its image unsteady<br />in the flowing stream</p>"))
  service ! Email("Haiku-2", Correspondent("john@doe.com", Some("John Doe")), to = List(Correspondent("foo@bar.com", Some("Foo Bar"))), html = Some("<p>an aging willow--<br />its image unsteady<br />in the flowing stream</p>"))
  service ! Email("Haiku-3", Correspondent("john@doe.com", Some("John Doe")), to = List(Correspondent("foo@bar.com", Some("Foo Bar"))), html = Some("<p>an aging willow--<br />its image unsteady<br />in the flowing stream</p>"))
  service ! Email("Haiku-4", Correspondent("john@doe.com", Some("John Doe")), to = List(Correspondent("foo@bar.com", Some("Foo Bar"))), html = Some("<p>an aging willow--<br />its image unsteady<br />in the flowing stream</p>"))
  service ! Email("Haiku-5", Correspondent("john@doe.com", Some("John Doe")), to = List(Correspondent("foo@bar.com", Some("Foo Bar"))), html = Some("<p>an aging willow--<br />its image unsteady<br />in the flowing stream</p>"))
  service ! Email("Haiku-6", Correspondent("john@doe.com", Some("John Doe")), to = List(Correspondent("foo@bar.com", Some("Foo Bar"))), html = Some("<p>an aging willow--<br />its image unsteady<br />in the flowing stream</p>"))

  // Shutdown the system
  Thread.sleep(5000)
  system.shutdown()

}
