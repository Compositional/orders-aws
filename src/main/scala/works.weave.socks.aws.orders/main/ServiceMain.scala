package works.weave.socks.aws.orders.main

import org.springframework.context.annotation.{AnnotationConfigApplicationContext, ComponentScan}
import works.weave.socks.aws.orders.http
import works.weave.socks.aws.orders.http.Server
import works.weave.spring.aws.{DynamoConnection, DynamoSchema}

import scala.reflect.ClassTag

/** Entrypoint for the orders web service
  */
object ServiceMain {
  def main(args : Array[String]) : Unit = {

    System.setProperty("org.jboss.logging.provider", "slf4j")

    val appContext = new AnnotationConfigApplicationContext(classOf[Config])
    def bean[T : ClassTag] : T = appContext.getBean(implicitly[ClassTag[T]].runtimeClass).asInstanceOf[T]

    def initSchema(): Unit = {
      val dynamo = bean[DynamoConnection]
      bean[DynamoSchema].createMissing(dynamo.client)
    }
    initSchema()

    bean[Server].run()
  }

  @ComponentScan(basePackages = Array("works.weave.socks.aws.orders", "works.weave.spring.aws"))
  class Config {
  }
}
