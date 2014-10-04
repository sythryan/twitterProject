Banno Twitter Streaming API Programming Assignment
==============
The Twitter Streaming API provides real­time access to public tweets. In this assignment you will build a Scala application that connects to the Streaming API and processes incoming tweets to compute various statistics. This is your chance to demonstrate to us that you’re capable of building a modern, functioning application using solid software development skills. Please submit your best possible work that you would be proud to show to others. The sample endpoint provides a random sample of approximately 1% of the full tweet stream.

Your app should consume this sample stream and keep track of the following:

•	Total number of tweets received
•	Average tweets per hour/minute/second
•	Top emojis in tweets
•	Percent of tweets that contains emojis
•	Top hashtags
•	Percent of tweets that contain a url
•	Percent of tweets that contain a photo url (pic.twitter.com or instagram)
•	Top domains of urls in tweets

The emoji­data project provides a convenient emoji.json file that you can use to determine which emoji unicode characters to look for in the tweet text. Your app should also provide some way to report these values to a user (periodically log toterminal, return from RESTful web service, etc). If there are other interesting statistics you’d like to collect, that would be great. There is no need to store this data in a database; keeping everything in­memory is fine. That said, you should think about how you would persist data if that was a requirement.
It’s very important that when your system receives a tweet, you do not block while doing all of the tweet processing. Twitter regularly sees 5700 tweets/second, so your app may likely receive 57 tweets/second, with higher burst rates. The app should also process tweets as concurrently as possible, to take advantage of all available computing resources. While this system doesn’t need to handle the full tweet stream, you should think about how you could scale up your app to handle such a high volume of tweets.

