package com.coiney.akka.mailer.protocol

sealed trait MailerResponse
case class Success(request: MailerRequest)
case class Failure(request: MailerRequest, cause: Throwable)