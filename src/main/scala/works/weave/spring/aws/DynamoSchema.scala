package works.weave.spring.aws

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.dynamodbv2.model._
import org.slf4j.LoggerFactory
import works.weave.spring.Ops._

import scala.collection.JavaConverters._

abstract class DynamoSchema(dynamoConnection: DynamoConnection) {

  val LOG = LoggerFactory.getLogger(getClass)

  def createMissing(client: AmazonDynamoDBClient) : Unit = {
    val tableNames = client.listTables().getTableNames.asScala.toSet

    schema { table =>
      val name = table.getTableName
      if (tableNames contains name) {
        LOG.info("Table '{}' present", name)
      } else {
        LOG.info("Table '{}' missing, creating...", name)
        client.createTable(table)
        LOG.info("Table '{}' created", name)
      }
    }
  }


  protected def schema(declare : CreateTableRequest => Any) : Unit

  val hash = KeyType.HASH
  val range = KeyType.RANGE

  final protected def keySchemaElement(name : String, keyType: KeyType) =
    new KeySchemaElement(name, keyType)

  final protected def attributeDefinition(name : String, scalarAttributeType: ScalarAttributeType) =
    new AttributeDefinition(name, scalarAttributeType)

  final protected def table(name : String,
            attributeDefinitions : Seq[AttributeDefinition],
            keySchema : Seq[KeySchemaElement],
            provisionedThrougput : ProvisionedThroughput
           ) : CreateTableRequest = (new CreateTableRequest()
      after (_.setTableName(name))
      after (_.setAttributeDefinitions(attributeDefinitions.asJava))
      after (_.setKeySchema(keySchema.asJava))
      after (_.setProvisionedThroughput(provisionedThrougput))
    )


}
