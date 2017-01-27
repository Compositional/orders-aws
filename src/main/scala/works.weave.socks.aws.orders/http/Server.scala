package works.weave.socks.aws.orders.http

import org.eclipse.jetty
import org.eclipse.jetty.servlet.ServletContextHandler
import org.glassfish.jersey.server.ServerProperties
import org.springframework.stereotype.Component
import works.weave.socks.aws.orders.presentation.MappingProvider
import works.weave.socks.aws.orders.presentation.resource.OrdersResource
import scala.collection.JavaConverters._

@Component
class Server {

  def run(): Unit = {

    val port = System.getenv().asScala.getOrElse("PORT", "80").toInt

    val context = new ServletContextHandler(ServletContextHandler.SESSIONS)
    context.setContextPath("/")

    val jettyServer = new jetty.server.Server(port)
    jettyServer.setHandler(context)

    val jerseyServlet = context.addServlet(
      classOf[org.glassfish.jersey.servlet.ServletContainer], "/*")
    jerseyServlet.setInitOrder(0)

    // Tells the Jersey Servlet which REST service/class to load.
    jerseyServlet.setInitParameter(
      ServerProperties.PROVIDER_CLASSNAMES,
      Vector[Class[_]]
        ( classOf[OrdersResource]
        , classOf[MappingProvider]
        ).map(_.getCanonicalName).mkString(";"))

    try {
      jettyServer.start()
      jettyServer.join()
    } finally {
      jettyServer.destroy()
    }
  }
}
