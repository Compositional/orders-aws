package works.weave.socks.aws.orders.repository.dynamo

import java.time.LocalDateTime
import java.util.UUID

import com.amazonaws.services.dynamodbv2.model.{AttributeValue, GetItemResult}
import org.springframework.stereotype.Component
import works.weave.socks.aws.orders.domain.CustomerOrder
import works.weave.socks.aws.orders.repository.OrderRepository
import works.weave.spring.aws.DynamoConnection

import scala.collection.JavaConverters._

@Component
class DynamoOrderRepository(dynamoConnection: DynamoConnection) extends OrderRepository {

  override def find(key: UUID): Option[CustomerOrder] = {
    val r: Option[GetItemResult] = Option(dynamoConnection.client.getItem("orders", Map("id" -> new AttributeValue("123")).asJava))

    r.map(rr => fromDB(rr.getItem))
  }

  private def fromDB(map : java.util.Map[String, AttributeValue]) : CustomerOrder = {
    CustomerOrder(
      id = UUID.fromString(map.get("id").getS),
      customerId = UUID.fromString(map.get("id").getS),
      date = LocalDateTime.parse(map.get("date").getS),
      total = map.get("total").getS.toFloat)
  }

}
