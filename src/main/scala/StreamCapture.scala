package com.twitterProject

import akka.actor.{ActorSystem, Actor, ActorRef, Props}
import akka.routing.RoundRobinRouter
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import twitter4j._

object StreamCapture extends App {

  val nrOfInstances = 5 // move to config file

  val streamer = ActorSystem("streamer")
  val streamStorage = streamer.actorOf(Props(new StreamStorageActor))
  val router = system.actorOf(Props(streamStorage).withRouter(
    RoundRobinRouter(nrOfInstances))
  )

}
