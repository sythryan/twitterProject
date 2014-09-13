package com.twitterProject

import akka.actor.{Actor, ActorRef}
import twitter4j._
import scala.concurrent.future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success, Random}
import java.text.DecimalFormat
import scala.collection.mutable

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

class TweetProcessingActor(streamStorageActor: ActorRef) extends Actor {
  def receive = {
    case e: Status => streamStorageActor ! IncomingTweet(e)
  }
}

class StreamStorageActor extends Actor {

  def receive = {
    case e: IncomingTweet => update(e.status)
    case Recieved => future(
      "===================================================================\n" +
      "| Running Time: " + runLength + " seconds\n" +
      "| Total: " + totalTweets + "\n" +
      "| Average hour/minute/second: " + tweetAvgHour + "/" + tweetAvgMinute + "/" + tweetAvgSecond + "\n" +
      "| Percent Url Tweets: " + percentFormat.format(totalUrlTweets / totalTweets) + "%\n" +
      "| Percent Picture Tweets: " + percentFormat.format(totalPicTweets / totalTweets) + "%\n" +
      "| Top Domains: " + topDomains(0) + ", " + topDomains(1) + ", " + topDomains(2) + "\n" +
      "| Top Hashtags: " + topHashtags(0) + ", " + topHashtags(1) + ", " + topHashtags(2) + "\n" +
      "| Top Emojis: " + topEmojis(0) + ", " + topEmojis(1) + ", " + topEmojis(2) + "\n" +
      "| Percent Emoji Tweets:  " + percentFormat.format(totalEmojis / totalTweets) + "%\n" +
      "| " + totalEmojis + " \n" + 
      "-------------------------------------------------------------------\n"
    ).onComplete {
      case Success(x) => println(x)
      case Failure(x) => println(x)
    }
  }

  val percentFormat = new DecimalFormat("#.0000")

  val startTime = System.currentTimeMillis / 1000
  def runLength = System.currentTimeMillis / 1000 - startTime
  def tweetAvgSecond = totalTweets / runLength
  def tweetAvgMinute = (totalTweets / (runLength / 60.0)).toInt
  def tweetAvgHour   = (totalTweets / (runLength / 3600.0)).toInt

  private[this] var totalTweets = 0
  private[this] var topEmojis = List(" ", " ", " ")
  private[this] var totalEmojis = 0
  private[this] var allEmojisAndCounts = Map((" ", 0))
  private[this] var topHashtags = List(" ", " ", " ")
  private[this] var allHashtagsAndCounts = Map((" ", 0))
  private[this] var totalUrlTweets = 0.0
  private[this] var totalPicTweets = 0.0
  private[this] var topDomains = List(" ", " ", " ")
  private[this] var allDomainsAndCount = Map((" ", 0))

  private[this] def update(status: Status): Unit = {
    totalTweets += 1 
    updateUrlTweets(extractURLs(status))
    updateHashtags(extractURLs(status))
    updateEmojis(extractEmojis(status))
  }

  private[this] def updateUrlTweets(urlTweets: List[String]): Unit = urlTweets.foreach{ url => 
    val domainUrl = extractDomain(url)
    allDomainsAndCount = updatedScores(domainUrl, allDomainsAndCount)
    topDomains = mostPopular(domainUrl, topDomains, allDomainsAndCount)
    totalUrlTweets += 1
    if (url.contains("instagram") || (url.contains("pic.twitter")))
      totalPicTweets += 1
  }

  private[this] def updateHashtags(maybeHashtags: List[String]): Unit = maybeHashtags.foreach{ hashtag =>
    allHashtagsAndCounts = updatedScores(hashtag, allHashtagsAndCounts)
    topHashtags = mostPopular(hashtag, topHashtags, allHashtagsAndCounts)
  }

  private[this] def updateEmojis(emojis: List[String]): Unit = emojis.foreach { emoji =>
    totalEmojis += 1
    allEmojisAndCounts = updatedScores(emoji, allEmojisAndCounts)
    topEmojis = mostPopular(emoji, topEmojis, allEmojisAndCounts)
  }

  private[this] def mostPopular(x: String, xs: List[String], scores: Map[String, Int]): List[String] = if (!xs.contains(x)) {
    x match {
      case e if(scores(e) > scores(xs(0))) => List(e, xs(1), xs(2))
      case e if(scores(e) > scores(xs(1))) => List(xs(0), e, xs(2)) 
      case e if(scores(e) > scores(xs(2))) => List(xs(0), xs(1), e)
      case _ => xs
    }
  } else xs

  private[this] def updatedScores(elem: String, scores: Map[String, Int]): Map[String, Int] =
    if (scores.keys.exists(_ == elem)) {
      val newPair = (elem -> (scores(elem) + 1))
      val newScores = scores - elem
      newScores + newPair
    } else {
      scores + (elem -> 1)
    } 

  private[this] def extractURLs(status: Status): List[String] = {
    val uRLs = status.getURLEntities
    uRLs.map(_.getDisplayURL).toList
  }

  private[this] def extractHashtags(status: Status): List[String] = {
    val hashtags = status.getHashtagEntities
    hashtags.map(_.getText).toList
  }

  private[this] def extractEmojis(status: Status): List[String] = "[^\u0000-\uFFFF]".r.findAllIn(status.getText).toList

  private[this] def extractDomain(url: String): String = url.takeWhile(_!='/')
}