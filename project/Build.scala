package akkaMailer

import sbt._
import Keys._
import sbtassembly.Plugin._
import AssemblyKeys._
import scoverage.ScoverageSbtPlugin.{instrumentSettings => scoverageSettings}
import com.typesafe.tools.mima.plugin.MimaPlugin.mimaDefaultSettings
import org.scoverage.coveralls.CoverallsPlugin.coverallsSettings
import com.typesafe.tools.mima.plugin.MimaKeys.previousArtifact
import sbtunidoc.Plugin._

object BuildSettings {
  val buildName                 = "akka-mailer"
  val buildVersion              = "0.2.0-SNAPSHOT"
  val buildScalaVersion         = "2.10.4"
  val buildJvmVersion           = "1.7"

  val buildHomepage             = Some(url("http://www.coiney.com"))
  val buildStartYear            = Some(2014)
  val buildDescription          = "An Akka based concurrent mailer service"
  val buildLicense              = Seq("BSD" -> url("http://opensource.org/licenses/BSD-3-Clause"))

  val buildOrganizationName     = "coiney.com"
  val buildOrganization         = "com.coiney"
  val buildOrganizationHomepage = Some(url("http://coiney.com"))


  val buildSettings = Seq(
    name                 := buildName,
    version              := buildVersion,
    scalaVersion         := buildScalaVersion,
    // organization info
    organization         := buildOrganization,
    organizationName     := buildOrganizationName,
    organizationHomepage := buildOrganizationHomepage,
    // project meta info
    homepage             := buildHomepage,
    startYear            := buildStartYear,
    description          := buildDescription,
    licenses             := buildLicense
  )
}

object Resolvers {
  val sunrepo              = "Sun Maven2 Repo" at "http://download.java.net/maven/2"
  val oraclerepo           = "Oracle Maven2 Repo" at "http://download.oracle.com/maven"
  val sonatypesnapshotrepo = "Sonatype Snapshot Repo" at "http://oss.sonatype.org/content/repositories/snapshots"
  val sonatypereleaserepo  = "Sonatype Release Repo" at "http://oss.sonatype.org/content/repositories/releases"
  val sonatypegithubrepo   = "Sonatype Github Repo" at "http://oss.sonatype.org/content/repositories/github-releases"
  val clojarsrepo          = "Clojars Repo" at "http://clojars.org/repo"
  val conjarsrepo          = "Conjars Repo" at "http://conjars.org/repo"
  val twitter4jrepo        = "Twitter 4j Repo" at "http://twitter4j.org/maven2"
  val twitterrepo          = "Twitter Repo" at "http://maven.twttr.com"
  val typesaferepo         = "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/"
  val sprayrepo            = "Spray Repo" at "http://repo.spray.io"
  val coineyrepo           = "Coiney Repo" at "http://archives.coiney.com:8888/repository/release/"
  val coineysnapshotrepo   = "Coiney Snapshot Repo" at "http://archives.coiney.com:8888/repository/snapshots/"
}

object Dependencies {
  val tsconfig    = "com.typesafe" % "config" % "1.2.0"
  val akkaactor   = "com.typesafe.akka" %% "akka-actor" % "2.3.4"
  val akkaslf4j   = "com.typesafe.akka" %% "akka-slf4j" % "2.3.4"
  val logback     = "ch.qos.logback" %  "logback-classic" % "1.1.1"
  val acemail     = "org.apache.commons" % "commons-email" % "1.3.2"
  val scalatest   = "org.scalatest" %% "scalatest" % "2.2.0" % "test"
  val scalamock   = "org.scalamock" %% "scalamock-scalatest-support" % "3.1.RC1" % "test"
  val scalacheck  = "org.scalacheck" %% "scalacheck" % "1.11.4" % "test"
  val akkatest    = "com.typesafe.akka" %% "akka-testkit" % "2.3.4" % "test"
}

object Utils {

  def module(name: String, sharedSettings: Seq[Setting[_]]): Project = {
    val id = s"${BuildSettings.buildName}-${name}"
    Project(
      id = id,
      base = file(id),
      settings = sharedSettings ++ Seq(
        Keys.name := id
      )
    )
  }

}

object Scalabuild extends Build {
  import Dependencies._
  import Resolvers._
  import BuildSettings._
  import Utils._

  val testDeps = Seq(
    scalatest,
    scalamock,
    scalacheck,
    akkatest
  )

  val commonDeps = Seq(
    tsconfig,
    logback,
    akkaactor,
    akkaslf4j,
    acemail
  )

  val commonResolvers = Seq(
    sunrepo,
    oraclerepo,
    sonatypesnapshotrepo,
    sonatypereleaserepo,
    sonatypegithubrepo,
    clojarsrepo,
    twitterrepo,
    twitter4jrepo,
    typesaferepo,
    sprayrepo,
    coineyrepo,
    coineysnapshotrepo
  )

  val publishSettings: Seq[Setting[_]] = Seq(
    publishTo := {
      val nexus = "http://archives.coiney.com:8888"
      if (isSnapshot.value)
        Some("snapshots" at nexus + "/repository/snapshots/")
      else
        Some("releases" at nexus + "/repository/release/")
    },
    publishMavenStyle := true,
    publishArtifact in Test := false,
    pomIncludeRepository := { _ => false },
    credentials += Credentials(Path.userHome / ".ivy2" / ".credentials_coiney_snapshots"),
    credentials += Credentials(Path.userHome / ".ivy2" / ".credentials_coiney_release"),
    pomExtra :=
      <url>https://github.com/coiney/akka-mailer</url>
      <licenses>
        <license>
          <name>BSD</name>
          <url>http://opensource.org/licenses/BSD-3-Clause</url>
          <distribution>repo</distribution>
        </license>
      </licenses>
      <scm>
        <url>git@github.com:coiney/akka-mailer.git</url>
        <connection>scm:git:git@github.com:coiney/akka-mailer.git</connection>
      </scm>
      <developers>
        <developer>
          <id>pjan</id>
          <name>pjan vandaele</name>
          <url>http://pjan.io</url>
        </developer>
      </developers>
  )

val sharedSettings = buildSettings ++ publishSettings ++ unidocSettings ++ mimaDefaultSettings ++ scoverageSettings ++ coverallsSettings ++ assemblySettings ++ Seq(
    libraryDependencies ++= (testDeps ++ commonDeps),
    resolvers ++= commonResolvers,
    scalacOptions ++= Seq("-encoding", "utf8"),
    scalacOptions += "-unchecked",
    scalacOptions += "-deprecation",
    scalacOptions += "-Yresolve-term-conflict:package",
    javacOptions ++= Seq("-source", buildJvmVersion, "-target", buildJvmVersion),
    javacOptions in doc := Seq("-source", buildJvmVersion),
    parallelExecution in Test := true
  )

  lazy val akkaMailer = Project(
    id = buildName,
    base = file("."),
    settings = sharedSettings
  ).settings(
    test := { },
    publish := { },
    publishLocal := { }
  ).aggregate(
    akkaMailerCore
  )

  lazy val akkaMailerCore = module(
    "core",
    sharedSettings
  )

  lazy val akkaMailerExample = module(
    "example",
    sharedSettings
  ).settings(
    publish := { },
    publishLocal := { }
  ).dependsOn(
    akkaMailerCore % "test->test;compile->compile"
  )

}
