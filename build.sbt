organization := "com.optrak"

name := "raptureTest"

version := "0.1-SNAPSHOT"

resolvers ++= Seq(
  "snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
  "sonatype.releases" at "https://oss.sonatype.org/content/repositories/releases/",
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
  "SS" at "http://oss.sonatype.org/content/repositories/snapshots/",
  "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases")

scalaVersion := "2.11.2"

libraryDependencies ++= {
  Seq(
    "commons-io" % "commons-io" % "2.4", 
    "org.slf4j" % "slf4j-api" % "1.7.7", 
    "ch.qos.logback" % "logback-classic" % "1.1.2", 
    "org.clapper" %% "grizzled-slf4j" % "1.0.2",
    "org.json4s" %% "json4s-native" % "3.2.10",
    // typelevel.org related libraries
    //"org.scalaz" %%  "scalaz-core" % "7.1.0",
    //"com.chuusai" %% "shapeless" % "2.0.0",
    //"org.typelevel" %% "shapeless-scalaz" % "0.1.3",
   // "joda-time" % "joda-time" % "2.5",
    "com.propensive" %% "rapture-json-json4s" % "1.0.6",
    "com.typesafe.play" %% "play-json" % "2.3.4",
    "com.optrak" %% "scalautil" % "latest.integration",
    // testing libraries
    "org.specs2" %% "specs2" % "2.4.6" % "test"  
  )
}

scalacOptions ++= Seq("-feature", "-deprecation")

parallelExecution in Test := false

exportJars := true
