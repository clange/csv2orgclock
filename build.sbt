val scala3Version = "3.3.1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "CSV to Org Clock",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,
    scalacOptions += "-deprecation",

    libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.18",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.18" % "test",
    libraryDependencies += "com.github.tototoshi" %% "scala-csv" % "1.3.10"
  )
