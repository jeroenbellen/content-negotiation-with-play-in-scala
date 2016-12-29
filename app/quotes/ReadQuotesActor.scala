package quotes

import akka.actor.Actor
import quotes.ReadQuotesActor.All

import scala.concurrent.Future

object ReadQuotesActor {

  case object All

}

class ReadQuotesActor extends Actor {

  import akka.pattern.pipe
  import context.dispatcher

  override def receive: Receive = {
    case All => Future {
      List(
        Quote("The future influences the present just as much as the past.", Author("Friedrich Nietzsche", "German")),
        Quote("A man without ethics is a wild beast loosed upon this world.", Author("Albert Camus", "French"))
      )
    } pipeTo sender
  }
}
