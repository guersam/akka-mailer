package com.coiney.akka.mailer.example

import akka.actor.ActorSystem

import com.coiney.akka.mailer.{Correspondent, Email, MailerSystem}


// example where you create master and dispatchers separately.
// This gives you granular control to put your master and dispatchers
// freely in the hierarchy of your actor tree.
object MailerExample1 extends App {

  implicit val system = ActorSystem("mailer-system")

  val mailerSystem = MailerSystem()

  val master = mailerSystem.createMaster("master")

  for(i <- 1 to 3) { mailerSystem.createDispatcher(master) }

  val service = mailerSystem.createService("mailer-service")

  // Send a couple of emails
  master ! Email("Haiku-1", Correspondent("john@doe.com", Some("John Doe")), to = List(Correspondent("foo@bar.com", Some("Foo Bar"))), html = Some("<p>an aging willow--<br />its image unsteady<br />in the flowing stream</p>"))
  master ! Email("Haiku-2", Correspondent("john@doe.com", Some("John Doe")), to = List(Correspondent("foo@bar.com", Some("Foo Bar"))), html = Some("<p>an aging willow--<br />its image unsteady<br />in the flowing stream</p>"))
  master ! Email("Haiku-3", Correspondent("john@doe.com", Some("John Doe")), to = List(Correspondent("foo@bar.com", Some("Foo Bar"))), html = Some("<p>an aging willow--<br />its image unsteady<br />in the flowing stream</p>"))
  master ! Email("Haiku-4", Correspondent("john@doe.com", Some("John Doe")), to = List(Correspondent("foo@bar.com", Some("Foo Bar"))), html = Some("<p>an aging willow--<br />its image unsteady<br />in the flowing stream</p>"))
  master ! Email("Haiku-5", Correspondent("john@doe.com", Some("John Doe")), to = List(Correspondent("foo@bar.com", Some("Foo Bar"))), html = Some("<p>an aging willow--<br />its image unsteady<br />in the flowing stream</p>"))
  master ! Email("Haiku-6", Correspondent("john@doe.com", Some("John Doe")), to = List(Correspondent("foo@bar.com", Some("Foo Bar"))), html = Some("<p>an aging willow--<br />its image unsteady<br />in the flowing stream</p>"))

  // Shutdown the system
  Thread.sleep(5000)
  system.shutdown()

}
