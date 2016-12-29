import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatestplus.play.{OneServerPerSuite, PlaySpec}

class QuotesResourceSpec extends PlaySpec
  with OneServerPerSuite
  with ScalaFutures
  with IntegrationPatience {

  "GET /quotes" must {

    "respond with 415 when called with an invalid media type" in {
      val response = wsUrl("/quotes").withHeaders("Accept" -> "application/json").get.futureValue

      response.status must be(415)
      response.statusText must be("Unsupported Media Type")
    }

    "respect V1 contract" in {
      val response = wsUrl("/quotes").withHeaders("Accept" -> "application/vnd.QuotesV1+json").get.futureValue

      response.status must be(200)
      response.header("Content-Type") must be(Some("application/vnd.QuotesV1+json"))
      response.header("Deprecated") must be(Some("Upgrade to application/vnd.QuotesV2+json"))

      val json = response.json

      (json \ 0 \ "quote").as[String] must be ("The future influences the present just as much as the past.")
      (json \ 0 \ "author").as[String] must be ("Friedrich Nietzsche")

      (json \ 1 \ "quote").as[String] must be ("A man without ethics is a wild beast loosed upon this world.")
      (json \ 1 \ "author").as[String] must be ("Albert Camus")
    }

    "respect V2 contract" in {
      val response = wsUrl("/quotes").withHeaders("Accept" -> "application/vnd.QuotesV2+json").get.futureValue

      response.status must be(200)
      response.header("Content-Type") must be(Some("application/vnd.QuotesV2+json"))
      response.header("Deprecated") must be(None)

      val json = response.json

      (json \ 0 \ "quote").as[String] must be ("The future influences the present just as much as the past.")
      (json \ 0 \ "author" \ "name").as[String] must be ("Friedrich Nietzsche")
      (json \ 0 \ "author" \ "nationality").as[String] must be ("German")

      (json \ 1 \ "quote").as[String] must be ("A man without ethics is a wild beast loosed upon this world.")
      (json \ 1 \ "author" \ "name").as[String] must be ("Albert Camus")
      (json \ 1 \ "author" \ "nationality").as[String] must be ("French")
    }
  }

}
