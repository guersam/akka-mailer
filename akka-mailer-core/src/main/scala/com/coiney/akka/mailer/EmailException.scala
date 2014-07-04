package com.coiney.akka.mailer


class EmailException(message: String, cause: Throwable) extends Exception(message, cause) with Serializable {
  def this(msg: String) = this(msg, null)
}
