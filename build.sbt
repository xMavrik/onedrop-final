name := """OneDrop Final"""
organization := "com.mavrik"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.6"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test

libraryDependencies += ws
//libraryDependencies += ehcache
libraryDependencies += caffeine


// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.mavrik.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.mavrik.binders._"
