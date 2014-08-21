name := "Twitter Project"
   
version := "1.0"

scalaVersion := "2.11.1"

libraryDependencies ++= {
  val akkaVersion = "2.3.3"
  Seq("com.typesafe.akka" %% "akka-actor" % akkaVersion,
      "org.twitter4j" % "twitter4j-stream" % "3.0.3")
}


