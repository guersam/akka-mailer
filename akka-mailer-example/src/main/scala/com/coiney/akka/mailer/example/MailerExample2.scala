package com.coiney.akka.mailer.example

import akka.actor.ActorSystem

import com.coiney.akka.mailer.{Correspondent, Email, MailerSystem}


// example where you create a mailer "service", a complete hierarchy
// consisting of your master and the number of dispatchers as given in the
// configuration. This has the "correct" supervisorStrategy, restarting
// the parts of the hierarchy necessary in case of Errors/Exceptions.
// The service actor is a proxy for the master, forwarding the messages.
object MailerExample2 extends App {

  implicit val system = ActorSystem("mailer-system")

  val mailerSystem = MailerSystem()

  val service = mailerSystem.createService("mailer-service")

  // Send a couple of emails
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
