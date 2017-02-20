package works.weave.socks.aws.orders.main

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.annotation.ComponentScan
import scala.reflect.ClassTag
import works.weave.socks.spring.aws.DynamoConfiguration
import works.weave.socks.spring.aws.DynamoSchema

/**
  * Entrypoint for the orders web service
  */
object ServiceMain {
  def main(args : Array[String]) : Unit = {

    System.setProperty("org.jboss.logging.provider", "slf4j")

    val appContext = new AnnotationConfigApplicationContext(classOf[Config])
    def bean[T : ClassTag] : T = appContext.getBean(implicitly[ClassTag[T]].runtimeClass).asInstanceOf[T]

    def initSchema() : Unit = {
      val dynamo = bean[DynamoConfiguration]
      bean[DynamoSchema].createMissing(dynamo.client)
    }
    def resetSchema() : Unit = {
      val dynamo = bean[DynamoConfiguration]
      bean[DynamoSchema].resetDestructively(dynamo.client)
    }

    // FIXME: do neither of initSchema, resetSchema
    //initSchema()
    //resetSchema()

    try {
      bean[Server].run()
    } catch {
      case e : Throwable if { Log.error("Service quitting due to throwable", e); false } =>
    } finally {
      Log.warn("Force-flushing log... " + (0 to 4096).map(_ => " ").mkString)
      System.err.flush()
      System.out.flush()
    }
  }

  @ComponentScan(basePackages = Array("works.weave.socks.aws.orders", "works.weave.socks.spring"))
  class Config {
  }

  val Log : Logger = LoggerFactory.getLogger(getClass)
}