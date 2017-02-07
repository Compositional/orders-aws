package works.weave.socks.aws.orders.repository.web

import java.net.URI
import org.springframework.stereotype.Component
import works.weave.socks.aws.orders.repository.CartRepository

@Component
class WebCartRepository extends CartRepository {
  override def findItemsByURI(uri : URI) : List[CartRepository.OrderItem] = {
    JSONHTTP.get[List[CartRepository.OrderItem]](uri)
  }
}
