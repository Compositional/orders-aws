package works.weave.socks.aws.orders.http

import javax.inject.Singleton

import org.glassfish.hk2.api.{InjectionResolver, TypeLiteral}
import org.glassfish.hk2.utilities.binding.AbstractBinder
import org.glassfish.jersey.model.ContractProvider
import org.glassfish.jersey.server.ResourceConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.{ApplicationContext, ApplicationContextAware}
import org.springframework.stereotype.Component
import works.weave.socks.aws.orders.presentation.MappingProvider
import works.weave.socks.aws.orders.presentation.resource.OrdersResource

@Component
class JerseyApp(ordersResource: OrdersResource) extends ResourceConfig with ApplicationContextAware {

  var applicationContext : ApplicationContext = _

  def reg(x : Object): Unit = {
    register(x, ContractProvider.NO_PRIORITY)
  }

  registerClasses(classOf[MappingProvider])
  reg(ordersResource)

  override def setApplicationContext(applicationContext: ApplicationContext): Unit = {
    this.applicationContext = applicationContext
  }

}