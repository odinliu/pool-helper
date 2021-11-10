name := "pool-helper"
organization := "com.odinliu.util"
version := "1.0.0"

scalaVersion := "2.11.8"
javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

lazy val scala12Version = "2.12.15"
lazy val scala13Version = "2.13.7"
lazy val supportedScalaVersions = List(scala12Version, scala13Version)

crossScalaVersions := supportedScalaVersions

libraryDependencies ++= Seq(
)

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases")
)

