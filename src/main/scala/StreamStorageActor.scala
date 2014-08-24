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
  private[this] var totalTweets = 0
  private[this] var averageTweets = 0
  private[this] var topEmojis = ""
  private[this] var percentEmojis = 0
  private[this] var topHashtags = ""
  private[this] var percentUrlTweets = 0
  private[this] var percentPhotoTweets = 0
  private[this] var topDomains = List("")

  def receive = {
    case IncomingTweet => incrementTotal
    case Recieved => future(totalTweets).onComplete {
      case Success(x) => println(x + "success")
      case Failure(x) => println(x + "fail")
    }
  }

  def incrementTotal: Unit = totalTweets += 1 
}

class TweetProcessingActor (streamStorageActor: ActorRef) extends Actor{
  def receive = {
    case e: Status => streamStorageActor ! IncomingTweet
  }
}