package com.coiney.akka.mailer.util

import com.coiney.akka.mailer.{EmailException, Email}


trait Mailer {
  @throws(classOf[EmailException])
  def sendEmail(email: Email): Unit
}