package com.coiney.akka.mailer.actors

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestProbe, TestKit}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

class DispatcherSpec(_actorSystem: ActorSystem) extends TestKit(_actorSystem)
                                                with ImplicitSender
                                                with WordSpecLike
                                                with Matchers
                                                with BeforeAndAfterAll
                                                with MailerSpec {

  def this() = this(ActorSystem("DispatcherSpec"))

  override def afterAll(): Unit = {
    system.shutdown()
  }

  "Dispatcher actor" should {

    "inform the master that it is created" in {
      val probe = TestProbe()
      val dispatcher = TestDispatcherRef(probe.ref)

      probe.expectMsg(Master.DispatcherCreated(dispatcher))
    }

    "request an email when being informed there's new ones available" in {
      val probe = TestProbe()
      val dispatcher = TestDispatcherRef(probe.ref)
      probe.expectMsg(Master.DispatcherCreated(dispatcher))

      probe.send(dispatcher, Dispatcher.MailsAvailable)

      probe.expectMsg(Master.MailRequest(dispatcher))
    }

    "process an email, confirm processing and request a new one" in {
      val probe = TestProbe()
      val dispatcher = TestDispatcherRef(probe.ref)
      val email = randomEmail()
      probe.expectMsg(Master.DispatcherCreated(dispatcher))

      probe.send(dispatcher, Dispatcher.SendMail(email))

      probe.expectMsg(Master.MailSent(dispatcher))
      probe.expectMsg(Master.MailRequest(dispatcher))
    }

  }

}
