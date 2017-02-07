package works.weave.socks.aws.orders.repository

import java.net.URI
import works.weave.socks.aws.orders.repository.CardRepository.Card

trait CardRepository {
  def findByURI(uri : URI) : Card
}

object CardRepository {
  case class Card(
    longNum : String,
    expires : String,
    ccv : String)

}