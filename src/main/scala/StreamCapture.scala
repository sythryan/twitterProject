package com.twitterProject

import akka.actor.{ActorSystem, Actor, ActorRef, Props}
import akka.routing.RoundRobinRouter
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import twitter4j._

object StreamCapture extends App
  with SimpleListener {

  val apiKey = "odXvyafpWSQlGtAlVX7tybp7O"
  val apiSecret = "gaKCGYnarfHZpEwHHF1hVMSColyAVMiigE2pBBomwLgMhVdbeH"
  val token = "1149304981-5lk07HSh0p3autlERVoaQS81SKZV4Rwo2qAohTR"
  val tokenSecret = "Wzm59pt6Oh0ZaaLVQWQFdBxTnmx71ndcvfBeM5FiZzD5m"

  val nrOfInstances = 5

  val streamer = ActorSystem("streamer")
  val streamStorage = streamer.actorOf(Props[StreamStorageActor])
  val router = streamer.actorOf(Props[StreamStorageActor].withRouter(
    RoundRobinRouter(nrOfInstances))
  )
  val theStream = new TwitterStreamFactory(
    new twitter4j.conf.ConfigurationBuilder()
    .setOAuthConsumerKey(apiKey)
    .setOAuthConsumerSecret(apiSecret)
    .setOAuthAccessToken(token)
    .setOAuthAccessTokenSecret(tokenSecret)
    .build
  ).getInstance

  theStream.addListener(simpleStatusListener(router))
  theStream.sample
  streamer.scheduler.schedule(3 seconds, 3 seconds, streamStorage, Recieved)
}

