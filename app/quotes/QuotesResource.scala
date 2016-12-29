package quotes

import javax.inject.{Inject, Named, Singleton}

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import play.api.http.HttpVerbs
import play.api.libs.json.Json
import play.api.mvc._
import quotes.ReadQuotesActor.All

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}


object QuotesRequest {
  def apply[A](request: Request[A]): QuotesRequest[A] = new QuotesRequest(request)
}

class QuotesAction @Inject()(implicit ex: ExecutionContext)
  extends ActionBuilder[QuotesRequest]
    with HttpVerbs {

  override def invokeBlock[A](request: Request[A], block: (QuotesRequest[A]) => Future[Result]): Future[Result] =
    block {
      QuotesRequest(request)
    }
}

class QuotesRequest[A](request: Request[A])
  extends WrappedRequest(request)


object ApiAccepts {
  val v1 = Accepting("application/vnd.QuotesV1+json")

  val v2 = Accepting("application/vnd.QuotesV2+json")
}

@Singleton
class QuotesResource @Inject()(quotesAction: QuotesAction,
                               @Named("readQuotesActor") readQuotesActor: ActorRef)
                              (implicit ex: ExecutionContext)
  extends Controller {

  implicit val timeout: Timeout = 50.millis

  def all: Action[AnyContent] = quotesAction async {
    implicit request => (readQuotesActor ? All).
      mapTo[List[Quote]].
      map {
        quotes => render {

          case ApiAccepts.v1() => Ok {
            Json.toJson {
              quotes map { q => Json.obj(
                "ref" -> q.ref,
                "quote" -> q.quote,
                "author" -> q.author.name)
              }
            }
          } withHeaders ("Deprecated" -> s"Upgrade to ${ApiAccepts.v2.mimeType}") as ApiAccepts.v1.mimeType

          case ApiAccepts.v2() => Ok {
            Json.toJson {
              quotes map { q => Json.obj(
                "ref" -> q.ref,
                "quote" -> q.quote,
                "author" -> Json.obj(
                  "ref" -> q.author.ref,
                  "name" -> q.author.name,
                  "nationality" -> q.author.nationality))
              }
            }
          } as ApiAccepts.v2.mimeType

          case _ => UnsupportedMediaType
        }

      }
  }

}
