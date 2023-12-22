import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider
import software.amazon.awssdk.core.SdkBytes
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.apigatewaymanagementapi.ApiGatewayManagementApiClient
import software.amazon.awssdk.services.apigatewaymanagementapi.model.PostToConnectionRequest
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import java.net.URI
import java.nio.charset.StandardCharsets


class SendMessage {
    private val ddb = DynamoDbClient.builder()
        .region(Region.US_EAST_1)
        .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
        .build()

    fun handler(event: Map<String, Any>): Map<String, Any> {
        val connections = try {
            ddb.scan { it.tableName("websocket-api-chat-app-tutorial-ConnectionsTable8000B8A1-1QEOAFC5C49SR") }.items()
        } catch (err: Exception) {
            return mapOf("statusCode" to 500)
        }
        val requestContext = (event["requestContext"] as? Map<String, Any>) ?: emptyMap()
        val connectionId = requestContext["connectionId"]?.toString()

        val domainName = requestContext["domainName"].toString()
        val stage = requestContext["stage"].toString()

        val apiEndpoint = URI.create("https://$domainName/$stage")
        val callbackAPI = ApiGatewayManagementApiClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .endpointOverride(apiEndpoint)
            .build()

        val message = event["body"].toString().let { body ->
            body.substringAfter("message\":").substringBefore("}").trim('"', ' ', ':')
        }

        val messageBytes = SdkBytes.fromString(message, StandardCharsets.UTF_8)
        val sendMessages = connections.map { connection ->
            val connectionIdAttributeValue: AttributeValue? = connection["connectionId"]
            val connectionIdString = connectionIdAttributeValue?.s()
            if (connectionIdString != connectionId) {
                try {
                    callbackAPI.postToConnection(
                        PostToConnectionRequest.builder()
                            .connectionId(connectionIdString)
                            .data(messageBytes)
                            .build()
                    )
                } catch (e: Exception) {
                    println(e.toString())
                }
            }
        }

        try {
            sendMessages.forEach { it }
        } catch (e: Exception) {
            println(e)
            return mapOf("statusCode" to 500)
        }
        return mapOf("statusCode" to 200)
    }
}
