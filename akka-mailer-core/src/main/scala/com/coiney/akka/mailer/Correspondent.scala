package com.coiney.akka.mailer


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
