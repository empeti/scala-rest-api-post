import java.io.FileNotFoundException
import java.util.Properties

import com.google.gson.Gson
import org.apache.http.client.methods.{CloseableHttpResponse, HttpPost}
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils

import scala.io.Source

object Logger extends App {
  println("Hello scala")

  // get uri from properties
  val gson = new Gson()
  val properties: Properties = loadProperties("application.properties")
  val loggerUri = properties.getProperty("nh.logger.uri")

  // send post request with message
  val response = post(loggerUri, "Test message")

  // process response
  val responseStatus = response.getStatusLine.getStatusCode
  val responseBody = EntityUtils.toString(response.getEntity)
  println(responseStatus + ": " + responseBody)

  def loadProperties(filePath: String): Properties = {
    val url = getClass.getResource(filePath)
    val properties: Properties = new Properties()

    if (url == null) {
      throw new FileNotFoundException("Properties file cannot be loaded")
    }
    val source = Source.fromURL(url)
    properties.load(source.bufferedReader())
    properties
  }

  def toJson(message: String): String = {
    gson.toJson(NhLoggerMessage(message))
  }

  def post(uri: String, message: String): CloseableHttpResponse = {
    val post = new HttpPost(uri)
    val messageEntity = new StringEntity(toJson(message), "utf-8")
    post.setEntity(messageEntity);

    val client = HttpClients.createDefault()
    client.execute(post)
  }
}