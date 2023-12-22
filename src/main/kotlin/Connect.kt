import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest

class Connect {

    private val dynamoDbClient: DynamoDbClient = DynamoDbClient.builder()
        .region(Region.US_EAST_1)
        .build()
    fun handler(event: Map<String, Any>): Map<String, Any> {
        return handlerConnectWebsocket(event)
    }
    private fun handlerConnectWebsocket(event: Map<String, Any>): Map<String, Any> {
        val requestContext = event["requestContext"]
        if (requestContext !is Map<*, *>) {
            return mapOf("statusCode" to 500, "message" to "Invalid request context")
        }
        val connectionId = (requestContext["connectionId"] ?: "").toString()
        val itemValues: MutableMap<String, AttributeValue> = HashMap()
        itemValues["connectionId"] = AttributeValue.builder().s(connectionId).build()
        val tableName = "websocket-api-chat-app-tutorial-ConnectionsTable8000B8A1-1QEOAFC5C49SR"
        val putItemRequest = PutItemRequest.builder()
            .tableName(tableName)
            .item(itemValues)
            .build()
        return try {
            dynamoDbClient.putItem(putItemRequest)
            mapOf("statusCode" to 200)
        } catch (e: Exception) {
            e.printStackTrace()
            mapOf("statusCode" to 500)
        }
    }
}
