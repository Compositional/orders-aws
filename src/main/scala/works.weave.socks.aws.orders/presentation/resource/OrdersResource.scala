package works.weave.socks.aws.orders.presentation.resource

import java.net.URI
import java.time.LocalDateTime
import javax.ws.rs.core.MediaType
import javax.ws.rs._

import org.slf4j.LoggerFactory
import works.weave.socks.aws.orders.presentation.value._
import OrdersResource._
import works.weave.socks.aws.orders.http.JSONHTTP

@Path("/orders")
class OrdersResource {
  @GET
  @Produces(Array(MediaType.APPLICATION_JSON))
  def orders : OrdersList = {

    Log.debug("GET /orders handler running")

    val order = Order(id = "id",
      customerId = "custId",
      customer = OrderCustomer(
        firstName = "John",
        lastName = "Doe",
        username = "jdoe",
        addresses = Nil,
        cards = Nil
      ),
      address = OrderAddress(
        number = "2 a #3",
        street = "Weaver Street",
        city = "Soxton",
        postcode = "F00B4R",
        country = "New Zealand"
      ),
      card = OrderCard(
        longNum = "1234-5678-9098-7654-3210",
        expires = "11/56",
        ccv = "123"
      ),
      items = Nil,
      shipment = None,
      date = LocalDateTime.now().toString,
      total = 42.0f
    )

    OrdersList(
      OrdersListEmbedded(
        List(order)
      ),
      null,
      null
    )

  }

  @POST
  @Produces(Array(MediaType.APPLICATION_JSON))
  def putOrder(order : OrderRequest) : Order = {

    Log.debug("POST /orders handler running")
    Log.info("order: {}", order)

    Log.info("address: {}", JSONHTTP.get[OrderAddress](order.address))
    Log.info("card: {}", JSONHTTP.get[OrderCard](order.card))
    Log.info("customer: {}", JSONHTTP.get[OrderCustomer](order.customer))
    Log.info("items: {}", JSONHTTP.get[List[OrderItems]](order.items))

    Order(
      null,null,null,null,null,null,null,null,null
    )
  }


  //Order(UUID.randomUUID(), UUID.randomUUID(), LocalDateTime.now(), total = 42.0f)
}
object OrdersResource {
  val Log = LoggerFactory.getLogger(classOf[OrdersResource])
}
