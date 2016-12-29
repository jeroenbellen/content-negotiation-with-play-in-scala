package quotes

import java.util.UUID

case class Quote(quote: String, author: Author, ref: String = UUID.randomUUID().toString) {

}
