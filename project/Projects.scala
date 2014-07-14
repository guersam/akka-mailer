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
        compile(typesafeConfig, logback, akkaActor, akkaSlf4j, apacheCommonsEMail) ++
        test(scalaTest, akkaTest)
    )

  lazy val exampleModule = module("example", basicSettings)
    .settings(noPublishing: _*)
    .settings(
      libraryDependencies ++=
        compile(typesafeConfig, logback, akkaActor, akkaSlf4j) ++
        test(scalaTest, akkaTest)
    ).dependsOn(
      coreModule % "test->test;compile->compile"
    )

  def module(name: String, basicSettings: Seq[Setting[_]]): Project = {
    val id = s"${Globals.name}-$name"
    Project(id = id, base = file(id), settings = basicSettings ++ Seq(Keys.name := id))
  }

  val noPublishing: Seq[Setting[_]] = Seq(publish := { }, publishLocal := { }, publishArtifact := false)

}
