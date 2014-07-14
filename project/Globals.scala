import sbt._

object Globals {
  val name                  = "akka-mailer"
  val scalaVersion          = "2.10.4"
  val jvmVersion            = "1.7"

  val homepage              = Some(url("http://www.coiney.com"))
  val startYear             = Some(2014)
  val summary               = "An concurrent mailer service, based on Akka."
  val description           = "An concurrent mailer service, based on Akka."
  val maintainer            = "pjan <pjan@coiney.com>"
  val license               = Some("BSD 3")

  val organizationName      = "Coiney Inc."
  val organization          = "com.coiney"
  val organizationHomepage  = Some(url("http://coiney.com"))

  val sourceUrl             = "https://github.com/Coiney/akka-mailer"
  val scmUrl                = "git@github.com:Coiney/akka-mailer.git"
  val scmConnection         = "scm:git:git@github.com:Coiney/akka-mailer.git"

  val serviceDaemonUser     = "admin"
  val serviceDaemonGroup    = "admin"

  val baseCredentials: Seq[Credentials] = Seq(
    Credentials(Path.userHome / ".ivy2" / ".credentials_coiney_snapshots"),
    Credentials(Path.userHome / ".ivy2" / ".credentials_coiney_release")
  )

  val snapshotRepo = Some("snapshots" at "http://archives.coiney.com:8888/repository/snapshots/")

  val pomDevelopers =
    <id>pjan</id><name>pjan vandaele</name><url>http://pjan.io</url>;

  val pomLicense =
    <license>
      <name>BSD</name>
      <url>http://opensource.org/licenses/BSD-3-Clause</url>
      <distribution>repo</distribution>
    </license>;

}
