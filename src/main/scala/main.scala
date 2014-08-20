import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.commons.io._

object main extends App {
  val httpClient = HttpClientBuilder.create().build()
  val request = new HttpGet("https://stream.twitter.com/1.1/statuses/sample.json")
  val stream = httpClient.execute(request)
  val response = IOUtils.toString((httpClient.execute(request).getEntity.getContent))
  println(response)
}