package com.coiney.akka.mailer.actors

import akka.actor.ActorSystem
import akka.testkit.{TestProbe, TestKit}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

class ServiceSpec(_actorSystem: ActorSystem) extends TestKit(_actorSystem)
                                             with WordSpecLike
                                             with Matchers
                                             with BeforeAndAfterAll
                                             with MailerSpec {

  def this() = this(ActorSystem("ServiceSpec"))

  override def afterAll(): Unit = {
    system.shutdown()
  }


  "A DispatcherSupervisor actor" should {

    "supervise the correct amount of dispatchers (according to config)" in {
      val masterProbe = TestProbe()
      val supervisor = TestDispatcherSupervisorRef(masterProbe.ref)

      supervisor.children should have size 4
    }

  }

}
