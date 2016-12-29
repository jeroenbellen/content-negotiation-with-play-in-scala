import com.google.inject.AbstractModule
import play.api.libs.concurrent.AkkaGuiceSupport
import quotes.ReadQuotesActor

class Module extends AbstractModule with AkkaGuiceSupport {

  override def configure(): Unit = {
    bindActor[ReadQuotesActor]("readQuotesActor")
  }
}
