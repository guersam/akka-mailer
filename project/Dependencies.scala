import sbt._

object Dependencies {

  object Versions {
    val akka                = "2.3.4"
    val apacheCommonsEmail  = "1.3.2"
    val aspectj             = "1.7.4"
    val scalajHttp          = "0.3.16"
    val kamon               = "0.3.1"
    val logback             = "1.1.2"
    val play                = "2.3.0"
    val slick               = "2.1.0-M2"
    val spray               = "1.3.1"
    val typesafeConfig      = "1.2.0"

    val scalaTest       = "2.2.0"
    val scalaMock       = "3.1.RC1"
    val scalaCheck      = "1.11.4"
  }

  val akkaActor           = "com.typesafe.akka"   %%  "akka-actor"                  % Versions.akka
  val akkaSlf4j           = "com.typesafe.akka"   %%  "akka-slf4j"                  % Versions.akka
  val akkaTest            = "com.typesafe.akka"   %%  "akka-testkit"                % Versions.akka
  val apacheCommonsEMail  = "org.apache.commons"  %   "commons-email"               % Versions.apacheCommonsEmail
  val logback             = "ch.qos.logback"      %   "logback-classic"             % Versions.logback
  val scalajHttp          = "org.scalaj"          %%  "scalaj-http"                 % Versions.scalajHttp
  val typesafeConfig      = "com.typesafe"        %   "config"                      % Versions.typesafeConfig

  val scalaTest           = "org.scalatest"       %%  "scalatest"                   % Versions.scalaTest
  val scalaMock           = "org.scalamock"       %%  "scalamock-scalatest-support" % Versions.scalaMock
  val scalaCheck          = "org.scalacheck"      %%  "scalacheck"                  % Versions.scalaCheck

  def compile    (modules: ModuleID*): Seq[ModuleID] = modules map (_ % "compile")
  def provided   (modules: ModuleID*): Seq[ModuleID] = modules map (_ % "provided")
  def test       (modules: ModuleID*): Seq[ModuleID] = modules map (_ % "test")
  def runtime    (modules: ModuleID*): Seq[ModuleID] = modules map (_ % "runtime")
  def container  (modules: ModuleID*): Seq[ModuleID] = modules map (_ % "container")

}
