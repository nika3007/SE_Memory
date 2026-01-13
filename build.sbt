val scala3Version = "3.7.3"

lazy val root = project
  .in(file("."))
  .settings(
    name := "memory",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    // 1. ScalaFX für die GUI
    libraryDependencies += "org.scalafx" %% "scalafx" % "20.0.0-R31",

    // 2. Google Guice für Dependency Injection
    libraryDependencies += "net.codingwell" %% "scala-guice" % "7.0.0",


    // 3. Tests
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.18" % Test,
    Test / javaOptions += "-Dtest.env=true",

    // JSON für File I/O
    libraryDependencies += "com.typesafe.play" %% "play-json" % "2.10.5",

    // XML für File I/O
    libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "2.4.0",



    // Compiler-Optionen
    scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked", "-Wconf:msg=Implicit parameters should be provided with a `using` clause:s"),

    // Fork für JavaFX erforderlich
    run / fork := false,

    // 4. sbt Scoverage Settings
    coverageEnabled := true, //Aktiviert die Test-Coverage erst
    coverageMinimumStmtTotal := 50, //Mindest-Coverage = 50 %
    coverageFailOnMinimum := false, //build schlägt NICHT fehl auch wenn Mindest-Coverage nicht erreicht wird. meckert nur

  )

/*
coverageEnabled := true
coverageMinimumStmtTotal := 50
coverageFailOnMinimum := false

// Main-Klasse (Memory.scala) von Coverage ausschließen
coverageExcludedFiles := ".*Memory.scala"
*/


//Compile / run / mainClass := Some("MemoryGame")
//Compile / run / javaOptions += "-Dfile.encoding=UTF-8"
