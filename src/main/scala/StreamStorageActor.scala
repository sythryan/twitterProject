package com.twitterProject

import akka.actor.{Actor, ActorRef}
import twitter4j._
import scala.concurrent.future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}
import java.text.DecimalFormat

// ● Total number of tweets received
// ● Average tweets per hour/minute/second
// ● Top emojis in tweets
// ● Percent of tweets that contains emojis
// ● Top hashtags
// ● Percent of tweets that contain a url
// ● Percent of tweets that contain a photo url (pic.twitter.com or instagram)
// ● Top domains of urls in tweets

case object Recieved
case class IncomingTweet(status: Status)

class Extractor(status: Status) {
  val text = status.getText
}

class StreamStorageActor extends Actor {
  private[this] var totalTweets = 0
  private[this] var topEmojis = List("")
  private[this] var percentEmojis = 0
  private[this] var topHashtags = List("")
  private[this] var totalUrlTweets = 0.0
  private[this] var totalPicTweets = 0.0
  private[this] var topDomains = List("")

  val percentFormat = new DecimalFormat("#.00")

  def receive = {
    case e: IncomingTweet => update(e.status)
    case Recieved => future(
      "===================================================================\n" +
      "| Running Time: " + runLength + " seconds\n" +
      "| Total: " + totalTweets + "\n" +
      "| Average hour/minute/second: " + tweetAvgHour + "/" + tweetAvgMinute + "/" + tweetAvgSecond + "\n" +
      "| Percent Url Tweets: " + percentFormat.format(totalUrlTweets / totalTweets) + "%\n" +
      "| Percent Picture Tweets: " + percentFormat.format(totalPicTweets / totalTweets) + "%\n" +
      "-------------------------------------------------------------------\n"
    ).onComplete {
      case Success(x) => println(x)
      case Failure(x) => println(x)
    }
  }

  val startTime = System.currentTimeMillis / 1000
  def runLength = System.currentTimeMillis / 1000 - startTime
  def tweetAvgSecond = totalTweets / runLength
  def tweetAvgMinute = (totalTweets / (runLength / 60.0)).toInt
  def tweetAvgHour   = (totalTweets / (runLength / 3600.0)).toInt

  private[this] def update(status: Status): Unit = {
    totalTweets += 1 
    updateUrlTweets(extractURLs(status))

  }

  private[this] def updateUrlTweets(urlTweets: List[String]): Unit = {
    totalUrlTweets += urlTweets.length
    val picTweets = urlTweets.filter(e => e.contains("instagram") || (e.contains("pic.twitter")))
    totalPicTweets += picTweets.length
  }

  private[this] def extractURLs(status: Status): List[String] = {
    val uRLs = status.getURLEntities
    uRLs.map(_.getDisplayURL).toList
  }

  private[this] def extractHashtags(status: Status): List[String] = {
    val hashtags = status.getHashtagEntities
    hashtags.map(_.getText).toList
  }
}

class TweetProcessingActor(streamStorageActor: ActorRef) extends Actor {
  def receive = {
    case e: Status => streamStorageActor ! IncomingTweet(e)
  }
}