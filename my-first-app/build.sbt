val scala3Version = "3.8.2"

lazy val root = project
  .in(file("."))
  .settings(
    name := "connect4-game",
    version := "0.2.0-16-4-25",

    scalaVersion := scala3Version,

    libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.14",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.14" % "test",
    libraryDependencies += "org.scalameta" %% "munit" % "1.2.4" % Test
  )
