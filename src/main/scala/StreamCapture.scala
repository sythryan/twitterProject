package com.twitterProject

import akka.actor.{ActorSystem, Actor, ActorRef, Props}
import akka.routing.RoundRobinRouter
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import twitter4j._

object StreamCapture extends App
  with Keys
  with SimpleListener {

  val nrOfInstances = 4

  val streamer = ActorSystem("streamer")
  val streamStorage = streamer.actorOf(Props[StreamStorageActor])
  val router = streamer.actorOf(Props(new TweetProcessingActor(streamStorage)).withRouter(
    RoundRobinRouter(nrOfInstances)
  ))
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

