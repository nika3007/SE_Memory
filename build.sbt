val scala3Version = "3.7.3"

lazy val root = project
  .in(file("."))
  .settings(
    name := "memory",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.18" % Test
  )

coverageEnabled := true
coverageMinimumStmtTotal := 50
coverageFailOnMinimum := false



//Compile / run / mainClass := Some("MemoryGame")
//Compile / run / javaOptions += "-Dfile.encoding=UTF-8"
