import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest

class Disconnect {
    private val ddb = DynamoDbClient.builder()
        .region(Region.US_EAST_1)
        .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
        .build()

    fun handleDisconnect(event: Map<String, Any>): Map<String, Any> {
        val requestContext = event["requestContext"] as Map<String, Any>
        val connectionId = requestContext["connectionId"].toString()
        val key = mapOf("connectionId" to AttributeValue.builder().s(connectionId).build())
        val deleteItemRequest = DeleteItemRequest.builder()
            .tableName("websocket-api-chat-app-tutorial-ConnectionsTable8000B8A1-1QEOAFC5C49SR")
            .key(key)
            .build()
        try {
            ddb.deleteItem(deleteItemRequest)
        } catch (e: Exception) {
            return mapOf("statusCode" to 500)
        }
        return mapOf("statusCode" to 200)
    }
}
