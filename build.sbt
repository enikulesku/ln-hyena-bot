name := "ln-hyena-bot"

version := "0.1"

scalaVersion := "2.12.5"

libraryDependencies += "info.mukel" %% "telegrambot4s" % "3.0.14"

mainClass in assembly := Some("HyenaBot")
assemblyJarName in assembly := "ln-hyena-bot.jar"