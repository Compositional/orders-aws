package works.weave.spring.aws

import javax.annotation.PostConstruct
import javax.inject.Inject

import com.amazonaws.ClientConfigurationFactory
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.dynamodbv2.model._
import org.springframework.context.{ApplicationContext, ApplicationContextAware}
import org.springframework.stereotype.Component
import works.weave.spring.Ops._

import scala.annotation.meta.beanSetter
import scala.beans.BeanProperty
import scala.collection.JavaConverters._

@Component
class DynamoConnection {

  lazy val endpoint = System.getenv().asScala.getOrElse("AWS_DYNAMODB_ENDPOINT", "http://dynamo:8000")

  lazy val client: AmazonDynamoDBClient = {
    System.err.println("EP: " + endpoint)
    new AmazonDynamoDBClient(DefaultAWSCredentialsProviderChain.getInstance(), new ClientConfigurationFactory().getConfig)
        .after(_.setEndpoint(endpoint))
  }

}
