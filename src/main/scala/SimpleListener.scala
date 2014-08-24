package com.twitterProject

import twitter4j.{StatusListener, StatusDeletionNotice, StallWarning, Status}
import akka.actor.ActorRef

trait SimpleListener {
  def simpleStatusListener(router: ActorRef) = new StatusListener() {
    def onStatus(status: Status) = router ! status
    def onDeletionNotice(statusDeletionNotice: StatusDeletionNotice) {}
    def onTrackLimitationNotice(numberOfLimitedStatuses: Int) {}
    def onException(ex: Exception) { ex.printStackTrace }
    def onScrubGeo(arg0: Long, arg1: Long) {}
    def onStallWarning(warning: StallWarning) {}
  }
}