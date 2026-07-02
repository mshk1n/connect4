val scala3Version = "3.8.2"

lazy val root = project
  .in(file("."))
  .settings(
    name := "connect4-game",
    version := "0.8.0-03-6-26",
    scalaVersion := scala3Version,

    libraryDependencies ++= Seq(
      "org.scalactic" %% "scalactic" % "3.2.14",
      "org.scalatest" %% "scalatest" % "3.2.14" % "test",
      "org.scalameta" %% "munit" % "1.2.4" % Test,
      "org.scala-lang.modules" %% "scala-swing" % "3.0.0",
      "com.google.inject" % "guice" % "7.0.0",
      "org.scala-lang.modules" %% "scala-xml" % "2.4.0",
      "org.playframework" %% "play-json" % "3.0.4"
    ),
    strykerIsSupported := true,
    fork := true,
    coverageExcludedPackages := "mXconnect"
  )