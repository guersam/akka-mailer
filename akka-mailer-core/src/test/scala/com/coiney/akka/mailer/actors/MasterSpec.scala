package com.coiney.akka.mailer.actors

import akka.actor.ActorSystem
import akka.testkit.{TestProbe, ImplicitSender, TestKit}
import com.coiney.akka.mailer.EmailException
import com.coiney.akka.mailer.actors.Master.DispatcherCreated
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import scala.concurrent.duration._


class MasterSpec(_actorSystem: ActorSystem) extends TestKit(_actorSystem)
                                            with WordSpecLike
                                            with Matchers
                                            with BeforeAndAfterAll
                                            with MailerSpec {

  def this() = this(ActorSystem("MasterSpec"))

  override def afterAll(): Unit = {
    system.shutdown()
  }

  "Master actor" should {

    "start with an empty email queue" in {
      val master = TestMasterRef()
      master.underlyingActor.emailQueue should have size 0
    }


    "add emails to the queue" in {
      val master = TestMasterRef()

      master ! randomEmail()
      master ! randomEmail()
      master ! randomEmail()

      master.underlyingActor.emailQueue should have size 3
    }

    "add emails to the queue with a reference to the sender, and an attempt counter of 1" in {
      val master = TestMasterRef()
      val probe = TestProbe()
      val email = randomEmail()

      probe.send(master, email)

      master.underlyingActor.emailQueue should contain ((probe.ref, Master.ScheduledEmail(email, 1)))
    }


    "start with no registered dispatchers" in {
      val master = TestMasterRef()
      master.underlyingActor.dispatchers should have size 0
    }


    "register an actor as dispatcher" in {
      val master = TestMasterRef()
      val probe = TestProbe()

      master ! DispatcherCreated(probe.ref)

      master.underlyingActor.dispatchers should have size 1
      master.underlyingActor.dispatchers should (contain key probe.ref and contain value None)
    }


    "not respond to a MailRequest if the email queue is empty" in {
      val master = TestMasterRef()
      val probe = TestProbe()

      master ! DispatcherCreated(probe.ref)
      probe.send(master, Master.MailRequest)

      probe.expectNoMsg(200.millis)
    }


    "send a MailsAvailable message to the dispatchers if there's emails in the queue (1)" in {
      val master = TestMasterRef()
      val probe = TestProbe()

      master ! randomEmail
      master ! DispatcherCreated(probe.ref)

      probe.expectMsg(Dispatcher.MailsAvailable)
    }


    "send a MailsAvailable message to the dispatchers if there's emails in the queue (2)" in {
      val master = TestMasterRef()
      val probe = TestProbe()

      master ! DispatcherCreated(probe.ref)
      master ! randomEmail

      probe.expectMsg(Dispatcher.MailsAvailable)
    }


    "respond to a MailRequest with a SendMail if there's (an) email(s) in the queue, and register the dispatcher with the email" in {
      val master = TestMasterRef()
      val dispatcherProbe = TestProbe()
      val senderProbe = TestProbe()
      val email = randomEmail()

      master ! DispatcherCreated(dispatcherProbe.ref)
      senderProbe.send(master, email)
      dispatcherProbe.expectMsg(Dispatcher.MailsAvailable)

      dispatcherProbe.send(master, Master.MailRequest(dispatcherProbe.ref))

      dispatcherProbe.expectMsg(Dispatcher.SendMail(email))
      master.underlyingActor.dispatchers should (contain key dispatcherProbe.ref and contain value Some((senderProbe.ref, Master.ScheduledEmail(email, 1))))
    }


    "respond to a MailRequest with a NoMails if there's no email in the queue" in {
      val master = TestMasterRef()
      val probe = TestProbe()

      master ! DispatcherCreated(probe.ref)

      probe.send(master, Master.MailRequest(probe.ref))

      probe.expectMsg(Dispatcher.NoMails)
    }


    "retry the SendEmail when sending failed" in {
      val master = TestMasterRef()
      val dispatcherProbe = TestProbe()
      val senderProbe = TestProbe()
      val email = randomEmail()

      // Inform the master that a dispatcher is created, and send an email, which the dispatcher should be informed about
      master ! DispatcherCreated(dispatcherProbe.ref)
      senderProbe.send(master, email)
      dispatcherProbe.expectMsg(Dispatcher.MailsAvailable)

      // Dispatcher requests the mail and gets it
      dispatcherProbe.send(master, Master.MailRequest(dispatcherProbe.ref))
      dispatcherProbe.expectMsg(Dispatcher.SendMail(email))

      // Dispatcher informs the master that it failed
      dispatcherProbe.send(master, Master.MailFailed(dispatcherProbe.ref, new EmailException("error")))

      // Master will retry sending the message. The dispatcher is informed about it's availability
      dispatcherProbe.expectMsg(1.second, Dispatcher.MailsAvailable)

      // Dispatcher requests the mail, and gets it
      dispatcherProbe.send(master, Master.MailRequest(dispatcherProbe.ref))
      dispatcherProbe.expectMsg(Dispatcher.SendMail(email))
    }


  }

}
