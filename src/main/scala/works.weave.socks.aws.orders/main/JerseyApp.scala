package works.weave.socks.aws.orders.main

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider
import java.io.OutputStream
import java.lang.annotation.Annotation
import java.lang.reflect.Type
import java.util.Locale
import java.util.UUID
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.Produces
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.MultivaluedMap
import javax.ws.rs.core.Response
import javax.ws.rs.ext.ExceptionMapper
import javax.ws.rs.ext.MessageBodyWriter
import javax.ws.rs.ext.Provider
import javax.xml.ws.http.HTTPException
import org.apache.http.impl.EnglishReasonPhraseCatalog
import org.glassfish.jersey.model.ContractProvider
import org.glassfish.jersey.server.ResourceConfig
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component
import works.weave.socks.aws.orders.main.JerseyApp.HALMessageBodyWriter
import works.weave.socks.aws.orders.presentation.MappingProvider
import works.weave.socks.aws.orders.presentation.resource.OrdersResource

@Component
class JerseyApp(ordersResource : OrdersResource) extends ResourceConfig with ApplicationContextAware {

  var applicationContext : ApplicationContext = _

  def reg(x : Object) : Unit = {
    register(x, ContractProvider.NO_PRIORITY)
  }

  registerClasses(classOf[MappingProvider])
  reg(ordersResource)
  reg(new JerseyApp.AnyExceptionForwardingExceptionMapper)
  reg(new JerseyApp.HTTPExceptionForwardingExceptionMapper)

  // We use the standard Jackson JSON Provider. HAL stuff is provided by the presentation class definitions,
  // such as works.weave.socks.aws.orders.presentation.value.Order
  reg(new HALMessageBodyWriter[Object](new JacksonJsonProvider()))

  override def setApplicationContext(applicationContext : ApplicationContext) : Unit = {
    this.applicationContext = applicationContext
  }

}
object JerseyApp {

  val Log = LoggerFactory.getLogger(classOf[JerseyApp])

  abstract class BaseExceptionMapper[T <: Throwable] extends ExceptionMapper[T] {
    @Context
    var request : HttpServletRequest = _

    def requestDescription : String = if (request == null) "" else {
      s"${request.getMethod} ${request.getPathInfo} ${request.getProtocol}"
    }
  }

  @Provider
  class AnyExceptionForwardingExceptionMapper extends BaseExceptionMapper[Exception] {

    override def toResponse(exception : Exception) : Response = {
      val exceptionId = UUID.randomUUID()
      Log.error(s"While handling request $requestDescription, caught exception, assigned it UUID $exceptionId, message: ${exception.getMessage}", exception)
      Response
        .status(500)
        .header(ExceptionIdHeader, exceptionId.toString)
        .entity(s"This should not have happened. We have encountered an unexpected problem and assigned it a unique code: $exceptionId")
        .`type`("text/plain")
        .build()
    }
  }

  @Provider
  class HTTPExceptionForwardingExceptionMapper extends BaseExceptionMapper[HTTPException] {

    override def toResponse(exception : HTTPException) : Response = {
      val exceptionId = Option(request).flatMap(rq => Option(rq.getHeader(ExceptionIdHeader))).getOrElse("")

      Log.warn(s"While handling request $requestDescription, Forwarding HTTP exception $exceptionId", exception)

      Response
        .status(exception.getStatusCode)
        .header(ExceptionIdHeader, exceptionId)
        .entity(reasonPhrase(exception))
        .`type`("text/plain")
        .build()
    }
  }

  def reasonPhrase(exception : HTTPException) : String = {
    EnglishReasonPhraseCatalog.INSTANCE.getReason(exception.getStatusCode, Locale.ENGLISH)
  }

  abstract class DelegatingMessageBodyWriter[T] extends MessageBodyWriter[T] {

    def messageBodyWriter : MessageBodyWriter[T]

    override def writeTo(t : T, `type` : Class[_], genericType : Type, annotations : Array[Annotation], mediaType : MediaType, httpHeaders : MultivaluedMap[String, AnyRef], entityStream : OutputStream) : Unit =
      messageBodyWriter.writeTo(t, `type`, genericType, annotations, mediaType, httpHeaders, entityStream)

    override def getSize(t : T, `type` : Class[_], genericType : Type, annotations : Array[Annotation], mediaType : MediaType) : Long =
      messageBodyWriter.getSize(t, `type`, genericType, annotations, mediaType)

    override def isWriteable(`type` : Class[_], genericType : Type, annotations : Array[Annotation], mediaType : MediaType) : Boolean =
      messageBodyWriter.isWriteable(`type`, genericType, annotations, mediaType)
  }

  /**
    * Provides a writer for application/hal+json.
    *
    * Actual writing is delegated to the messageBodyWriter object.
    *
    * @param messageBodyWriter
    * @tparam T
    */
  @Produces(Array("application/hal+json"))
  class HALMessageBodyWriter[T](val messageBodyWriter : MessageBodyWriter[T]) extends DelegatingMessageBodyWriter[T]

  val ExceptionIdHeader : String = "X-Exception-Id"
}