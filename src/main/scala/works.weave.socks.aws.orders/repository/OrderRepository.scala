package works.weave.socks.aws.orders.repository

import java.util.UUID
import works.weave.socks.aws.orders.domain.CustomerOrder

trait OrderRepository extends Repository[UUID, CustomerOrder] {

}
