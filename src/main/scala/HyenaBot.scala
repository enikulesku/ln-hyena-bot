import java.io.File

import info.mukel.telegrambot4s.Implicits._
import info.mukel.telegrambot4s.api._
import info.mukel.telegrambot4s.api.declarative.{Commands, InlineQueries}
import info.mukel.telegrambot4s.methods.ParseMode

import scala.concurrent.Future

/**
  * Let me Google that for you!
  */
case class LNHyenaBot(token: String, soundPath: String, playCommand: String) extends TelegramBot
  with Polling
  with InlineQueries
  with Commands {

  val extension = ".mp3"

  onCommand('start, 'help) { implicit msg =>
    reply(
      s"""LN Hyena Bot.
         |
         |/start | /help - list commands
         |
         |/play - list sounds
         |
         |/play args - play sound
      """.stripMargin,
      parseMode = ParseMode.Markdown)
  }

  onCommand('play) { implicit msg =>
    withArgs {
      case Nil =>
        val files = new File(soundPath).listFiles(_.getName.endsWith(extension))
            .map(_.getName.dropRight(extension length))
        reply(
          files.map(file => s"/play $file").mkString("\n\n")
        )
      case args =>
        val query = args.mkString(" ")
        Future(playSound(query))
        reply(
          s"$query - is playing",
          disableWebPagePreview = true,
          parseMode = ParseMode.Markdown
        )
    }
  }

  def playSound(query: String): Unit = {
    try {
      import sys.process._
      s"$playCommand $soundPath/$query$extension" !
    } catch {
      case e: Exception =>
        print(e)
    }
  }
}

object HyenaBot extends App {
  LNHyenaBot(args(0), args(1), args(2)).run()
}