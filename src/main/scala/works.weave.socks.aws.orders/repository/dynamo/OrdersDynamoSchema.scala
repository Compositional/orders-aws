package works.weave.socks.aws.orders.repository.dynamo

import com.amazonaws.services.dynamodbv2.model._
import org.springframework.stereotype.Component
import works.weave.spring.aws.{DynamoConnection, DynamoSchema}
import works.weave.spring.aws.DynamoSchema

/** Declares the DynamoDB schema
  */
@Component
class OrdersDynamoSchema(dynamoConnection: DynamoConnection) extends DynamoSchema(dynamoConnection) {

  override protected def schema(declare: (CreateTableRequest) => Any): Unit = {

    declare(
      table(name = "orders",
        attributeDefinitions = Seq(
          attributeDefinition("id", ScalarAttributeType.S),
          attributeDefinition("name", ScalarAttributeType.S)
        ),
        keySchema = Seq(
          keySchemaElement("id", KeyType.HASH),
          keySchemaElement("name", KeyType.RANGE)
        ),
        provisionedThrougput = new ProvisionedThroughput(1L, 1L)
      )
    )

  }
}
