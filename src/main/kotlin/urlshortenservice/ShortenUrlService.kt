package urlshortenservice

interface ShortenUrlService {
  fun shorten(longUrl: String = "https://en.wikipedia.org/wiki/Cache_replacement_policies#Last_in_first_out_(LIFO)"): String
}