package com.coiney.akka.mailer


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
) {
  require(subject != "", "Missing subject")
  require(from.email != "", "Missing from email")
  require(to != Nil, "Missing destination email")
  require(to.head.email != "", "Missing destination email")
  require(text != None || html != None, "Missing email body (text or html)")
}