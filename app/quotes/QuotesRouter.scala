package quotes

import javax.inject.Inject

import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing.sird._


class QuotesRouter @Inject()(quotesResource: QuotesResource) extends SimpleRouter {

  override def routes: Routes = {
    case GET(p"/quotes") => quotesResource.all
  }
}
