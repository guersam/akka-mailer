package com.coiney.akka.mailer


case class Correspondent (
  email: String,
  name: Option[String] = None
)
