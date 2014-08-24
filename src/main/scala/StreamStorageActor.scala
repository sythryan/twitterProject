package com.twitterProject

import akka.actor.{Actor, ActorRef}
import twitter4j._
import scala.concurrent.future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

// ● Total number of tweets received
// ● Average tweets per hour/minute/second
// ● Top emojis in tweets
// ● Percent of tweets that contains emojis
// ● Top hashtags
// ● Percent of tweets that contain a url
// ● Percent of tweets that contain a photo url (pic.twitter.com or instagram)
// ● Top domains of urls in tweets

case object Recieved
case object IncomingTweet

class StreamStorageActor extends Actor {
  private[this] var bigTotalTweets = 0
  private[this] var totalTweets = 0
  private[this] var topEmojis = List("")
  private[this] var percentEmojis = 0
  private[this] var topHashtags = List("")
  private[this] var percentUrlTweets = 0
  private[this] var percentPhotoTweets = 0
  private[this] var topDomains = List("")

  def receive = {
    case IncomingTweet => update
    case Recieved => future(
      "Total: " + bigTotalTweets + totalTweets + "\n" +
      "Average hour/minute/second: " + tweetAvgHour + "/" + tweetAvgMinute + "/" + tweetAvgSecond + "\n"
    ).onComplete {
      case Success(x) => println(x)
      case Failure(x) => println(x)
    }
  }

  val startTime = System.currentTimeMillis / 1000
  def currentTime = System.currentTimeMillis / 1000
  def tweetAvgSecond = totalTweets / (currentTime - startTime)
  def tweetAvgMinute = (totalTweets / ((currentTime - startTime) / 60.0)).toInt
  def tweetAvgHour   = (totalTweets / ((currentTime - startTime) / 3600.0)).toInt

  private[this] def update: Unit = {
    if (totalTweets > 2100000000) {
      totalTweets -= 2100000000
      bigTotalTweets += 1
    } else {totalTweets += 1}
  }
}

class TweetProcessingActor(streamStorageActor: ActorRef) extends Actor{
  def receive = {
    case e: Status => streamStorageActor ! IncomingTweet
  }
}