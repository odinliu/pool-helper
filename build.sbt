organization := "com.odinliu.util"
version := "1.0.0"
description  := "Some useful pool helper. A pure Scala library."
homepage     := Some(url("https://github.com/odinliu/pool-helper"))
licenses     := Seq("Apache License 2.0" -> url("https://github.com/odinliu/pool-helper/blob/main/LICENSE"))

lazy val root = (project in file("."))
  .settings(
    name := "pool-helper",
    scalaVersion := "2.12.12",
    crossScalaVersions := Seq("2.13.3", "2.12.12", "2.11.8"),
)

scmInfo := Some(
  ScmInfo(
    url("https://github.com/odinliu/pool-helper"),
    "scm:git@github.com:odinliu/pool-helper.git"
  )
)
developers := List(
  Developer(
    id    = "odinliu",
    name  = "Liu Yiding",
    email = "odinushuaia@gmail.com",
    url   = url("https://odinliu.com/")
  )
)
publishTo := {
  val nexus = "https://oss.sonatype.org/"
  val v = (version).value
  if (v.endsWith("-SNAPSHOT")) Some("snapshots" at nexus + "content/repositories/snapshots")
  else Some("releases" at nexus + "service/local/staging/deploy/maven2")
}
