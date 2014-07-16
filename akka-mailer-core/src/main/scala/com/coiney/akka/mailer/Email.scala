package com.coiney.akka.mailer


object Email {
  def apply(subject: String, from: Correspondent, to: Correspondent, html: String): Email =
    Email(subject, from, List(to), html = Some(html))
}

case class Email (
  subject: String,
  from: Correspondent,
  to: List[Correspondent],
  cc: List[Correspondent] = Nil,
  bcc: List[Correspondent] = Nil,
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