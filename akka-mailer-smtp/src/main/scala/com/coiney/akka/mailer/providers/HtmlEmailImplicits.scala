package com.coiney.akka.mailer.providers

import org.apache.commons.mail.HtmlEmail

import com.coiney.akka.mailer.protocol.{Email, Correspondent}


object HtmlEmailImplicits {
  implicit class HtmlEMailWrapper(mail: HtmlEmail) {
    import collection.JavaConversions._

    def setFrom(c: Correspondent): HtmlEmail = {
      c match {
        case Correspondent(email, None)       => mail.setFrom(email)
        case Correspondent(email, Some(name)) => mail.setFrom(email, name)
      }
      mail
    }

    def setReplyTo(c: Option[Correspondent]): HtmlEmail = {
      if (c.nonEmpty) c.get match {
        case Correspondent(email, None)       => mail.addReplyTo(email)
        case Correspondent(email, Some(name)) => mail.addReplyTo(email, name)
      }
      mail
    }

    def addTo(cs: List[Correspondent]): HtmlEmail = addCorrespondent(cs)(mail.addTo(_), mail.addTo(_, _))

    def addCc(cs: List[Correspondent]): HtmlEmail = addCorrespondent(cs)(mail.addCc(_), mail.addCc(_, _))

    def addBcc(cs: List[Correspondent]): HtmlEmail = addCorrespondent(cs)(mail.addBcc(_), mail.addBcc(_, _))

    def setHtmlMsg(msg: Option[String]): HtmlEmail = {
      msg.foreach(mail.setHtmlMsg)
      mail
    }

    def setTextMsg(msg: Option[String]): HtmlEmail = {
      msg.foreach(mail.setTextMsg)
      mail
    }

    def composeEmail(email: Email): HtmlEmail = {
      mail.setSubject(email.subject)
      mail.setFrom(email.from)
      mail.addTo(email.to)
      mail.addCc(email.cc)
      mail.addBcc(email.bcc)
      mail.setHtmlMsg(email.html)
      mail.setTextMsg(email.text)
      mail.setReplyTo(email.replyTo)
      mail.setHeaders(email.headers)
      mail
    }

    private def addCorrespondent(cs: List[Correspondent])(f: (String) => Unit, g: (String, String) => Unit): HtmlEmail = {
      cs.foreach {
        case Correspondent(email, None)       => f(email)
        case Correspondent(email, Some(name)) => g(email, name)
      }
      mail
    }
  }
}