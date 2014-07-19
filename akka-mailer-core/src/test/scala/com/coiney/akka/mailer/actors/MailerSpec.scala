package com.coiney.akka.mailer.actors

import akka.actor.ActorRef
import akka.testkit.{TestKit, TestActorRef}
import com.coiney.akka.mailer.{Correspondent, Email, MailerSystem}
import com.typesafe.config.{Config, ConfigFactory}

trait MailerSpec {
  this: TestKit =>

  val config= ConfigFactory.parseString(
    """
      |mailer.provider: "com.coiney.akka.mailer.providers.TestMailerProvider"
      |mailer.nr-of-dispatchers: 1
      |mailer.max-nr-of-retries: 2
      |mailer.retry-after: 200 ms
    """.stripMargin)

  def TestMasterRef(): TestActorRef[Master] =
    TestActorRef(Master(new MailerSystem.Settings(MailerSystem.findClassLoader(), config)))

  def DispatcherActorRef(master: ActorRef): TestActorRef[Dispatcher] =
    TestActorRef(Dispatcher(master, new MailerSystem.Settings(MailerSystem.findClassLoader(), config)))

  def randomEmail(): Email = {
    val randomString = util.Random.nextString(10)
    Email(randomString, Correspondent("foo@bar.com"), Correspondent("foo@baz.com"), s"<htm><body>$randomString</body></html>")
  }

}
