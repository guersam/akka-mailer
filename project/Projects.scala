import sbt._
import sbt.Keys._


object Projects extends Build {
  import Settings._
  import Unidoc.{settings => unidocSettings}
  import Assembly.{settings => assemblySettings}
  import Package.{serverSettings => packageServerSettings, rpmSettings => packageRpmSettings}
  import Release.{settings => releaseSettings}
  import AspectJ.{settings => aspectJSettings}
  import Dependencies._

  lazy val root = Project(id = Globals.name, base = file("."))
    .settings(basicSettings: _*)
    .settings(noPublishing: _*)
    .aggregate(
      coreModule,
      exampleModule
    )

  lazy val coreModule = module("core", basicSettings)
    .settings(unidocSettings: _*)
    .settings(assemblySettings: _*)
    .settings(releaseSettings: _*)
    .settings(
      libraryDependencies ++=
        compile(typesafeConfig, akkaActor) ++
        test(scalaTest, akkaTest)
    )

  lazy val smtpModule = module("smtp", basicSettings)
    .settings(unidocSettings: _*)
    .settings(assemblySettings: _*)
    .settings(releaseSettings: _*)
    .settings(
      libraryDependencies ++=
        compile(typesafeConfig, akkaActor, apacheCommonsEMail) ++
          test(scalaTest, akkaTest)
    ).dependsOn(
      coreModule % "test->test;compile->compile"
    )

  lazy val sendgridModule = module("sendgrid", basicSettings)
    .settings(unidocSettings: _*)
    .settings(assemblySettings: _*)
    .settings(releaseSettings: _*)
    .settings(
      libraryDependencies ++=
        compile(typesafeConfig, akkaActor, scalajHttp) ++
          test(scalaTest, akkaTest)
    ).dependsOn(
      coreModule % "test->test;compile->compile"
    )

  lazy val exampleModule = module("example", basicSettings)
    .settings(noPublishing: _*)
    .settings(
      libraryDependencies ++=
        compile(typesafeConfig, akkaActor) ++
        test(scalaTest, akkaTest)
    ).dependsOn(
      coreModule % "test->test;compile->compile",
      smtpModule,
      sendgridModule
    )

  def module(name: String, basicSettings: Seq[Setting[_]]): Project = {
    val id = s"${Globals.name}-$name"
    Project(id = id, base = file(id), settings = basicSettings ++ Seq(Keys.name := id))
  }

  val noPublishing: Seq[Setting[_]] = Seq(publish := { }, publishLocal := { }, publishArtifact := false)

}
