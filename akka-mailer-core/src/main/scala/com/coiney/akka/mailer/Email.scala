package com.coiney.akka.mailer


case class Email (
  subject: String,
  from: Correspondent,
  to: List[Correspondent],
  cc: List[Correspondent] = Nil,
  bcc: List[Correspondent] = Nil,
  html: Option[String] = None,
  text: Option[String] = None
) {
  require((to.size + cc.size + bcc.size) > 0, "An email needs at least one [to/cc/bcc] recipient.")
}