package com.twitterProject

import akka.actor.Actor

// ● Total number of tweets received
// ● Average tweets per hour/minute/second
// ● Top emojis in tweets
// ● Percent of tweets that contains emojis
// ● Top hashtags
// ● Percent of tweets that contain a url
// ● Percent of tweets that contain a photo url (pic.twitter.com or instagram)
// ● Top domains of urls in tweets


class StreamStorageActor extends Actor {
  case class TweetValues(
    var totalTweets: Int,
    var averageTweets: Int,
    var topEmojis: String,
    var percentEmojis: Int,
    var topHashtags: String,
    var percentUrlTweets: Int,
    var percentPhotoTweets: Int,
    var topDomains: List[String]
  )
  val TweetInfo = new TweetValues(0,0,"",0,"",0,0,List())
}