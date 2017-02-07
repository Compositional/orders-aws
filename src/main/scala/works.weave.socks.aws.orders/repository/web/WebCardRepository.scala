package works.weave.socks.aws.orders.repository.web

import java.net.URI
import org.springframework.stereotype.Component
import works.weave.socks.aws.orders.repository.CardRepository
import works.weave.socks.aws.orders.repository.CardRepository.Card

@Component
class WebCardRepository extends CardRepository {
  override def findByURI(uri : URI) : Card = {
    JSONHTTP.get[Card](uri)
  }
}
