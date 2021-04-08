package urlshortenservice

import java.io.BufferedReader
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import java.net.URLEncoder

class TinyUrl : ShortenUrlService {
  /**
   * More info:
   * - `curl https://tinyurl.com/api-create.php?url="{query}"`
   * - https://stackoverflow.com/questions/724043/http-url-address-encoding-in-java
   * - https://stackoverflow.com/questions/1485708/how-do-i-do-a-http-get-in-java
   */
  override fun shorten(longUrl: String): String {
    val uri = with(URL(longUrl)) { URI(protocol, userInfo, host, port, path, query, ref) }
    val tinyUri = "https://tinyurl.com/api-create.php?url=${URLEncoder.encode(uri.toURL().toString(), "UTF-8")}"
    return (URL(tinyUri).openConnection() as HttpURLConnection).apply {
      requestMethod = "GET"
    }.inputStream.bufferedReader().use(BufferedReader::readText)
  }

}
