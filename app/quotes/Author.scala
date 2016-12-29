package quotes

import java.util.UUID

case class Author(name: String, nationality: String, ref: String = UUID.randomUUID().toString) {

}
