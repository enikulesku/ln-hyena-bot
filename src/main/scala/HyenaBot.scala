import java.io.File

import info.mukel.telegrambot4s.Implicits._
import info.mukel.telegrambot4s.api._
import info.mukel.telegrambot4s.api.declarative.{Args, Commands, InlineQueries}
import info.mukel.telegrambot4s.methods.ParseMode
import info.mukel.telegrambot4s.models.Message

import scala.concurrent.Future
import scala.util.Random

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
         |/stop - stop all records
         |/play - list sounds
         |
         |/play args - play sound
         |
         |/playRandom - play random sound
      """.stripMargin,
      parseMode = ParseMode.Markdown)
  }

  onCommand('stop) { implicit msg =>
    import sys.process._
    s"killall $playCommand" !

    reply(
      "Stopped",
      parseMode = ParseMode.Markdown)
  }

  onCommand('playRandom) { implicit msg =>
      play(Random.shuffle(listSounds).head)
  }

  onCommand('play) { implicit msg =>
    withArgs {
      case Nil =>
        val files = listSounds
        reply(
          files.map(file => s"/play $file").mkString("\n\n")
        )
      case args =>
        val query = args.mkString(" ")
        play(query)
    }
  }

  def listSounds: List[String] = {
    new File(soundPath).listFiles(_.getName.endsWith(extension))
      .map(_.getName.dropRight(extension length)).toList
  }

  def play(query: String)(implicit msg: Message) = {
    Future(playSound(query))
    reply(
      s"$query - is playing",
      disableWebPagePreview = true,
      parseMode = ParseMode.Markdown
    )
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