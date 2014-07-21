package com.coiney.akka.mailer.protocol


sealed trait MailerRequest
object Email {
  def apply(subject: String, from: Correspondent, to: Correspondent, html: String): Email =
    Email(subject, from, List(to), html = Some(html))
  def apply(subject: String, from: Correspondent, to: Correspondent, html: String, text: String): Email =
    Email(subject, from, List(to), html = Some(html), text = Some(text))
  def apply(subject: String, from: Correspondent, to: Correspondent, cc: List[Correspondent], bcc: List[Correspondent], html: String): Email =
    Email(subject, from, List(to), cc, bcc, html = Some(html))
  def apply(subject: String, from: Correspondent, to: Correspondent, cc: List[Correspondent], bcc: List[Correspondent], html: String, text: String): Email =
    Email(subject, from, List(to), cc, bcc, html = Some(html), text = Some(text))
}

case class Email (
    subject: String,
    from: Correspondent,
    to: List[Correspondent],
    cc: List[Correspondent] = Nil,
    bcc: List[Correspondent] = Nil,
    replyTo: Option[Correspondent] = None,
    html: Option[String] = None,
    text: Option[String] = None,
    headers: Map[String, String] = Map.empty[String, String]
) extends MailerRequest {
  require(subject != "", "Missing subject")
  require(from.email != "", "Missing from email")
  require(to != Nil, "Missing destination email")
  require(to.head.email != "", "Missing destination email")
  require(text != None || html != None, "Missing email body (text or html)")
}


object Correspondent {
  def apply(email: String, name: String): Correspondent = Correspondent(email, Some(name))
  val validEmailFormat = """^(?!\.)(""([^""\r\\]|\\[""\r\\])*""|([-a-z0-9!#$%&'*+/=?^_`{|}~]|(?<!\.)\.)*)(?<!\.)@[a-z0-9][\w\.-]*[a-z0-9]\.[a-z][a-z\.]*[a-z]$""".r
}

case class Correspondent (
    email: String,
    name: Option[String] = None
) {
  import Correspondent._
  require(email.matches(validEmailFormat.toString()), "email address is invalid")
}