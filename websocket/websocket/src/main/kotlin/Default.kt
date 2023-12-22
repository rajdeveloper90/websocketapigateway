import software.amazon.awssdk.core.SdkBytes
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.apigatewaymanagementapi.ApiGatewayManagementApiClient
import software.amazon.awssdk.services.apigatewaymanagementapi.model.GetConnectionRequest
import software.amazon.awssdk.services.apigatewaymanagementapi.model.PostToConnectionRequest
import java.net.URI

class Default {
    public fun handler(event: Map<String, Any>): Map<String, Any> {
        val requestContext = (event["requestContext"] as? Map<String, Any>) ?: emptyMap()
        val connectionId = requestContext["connectionId"]?.toString()
        val domainName = requestContext["domainName"].toString()
        val stage = requestContext["stage"].toString()
        val apiEndpoint = URI.create("https://$domainName/$stage")
        val callbackAPI = ApiGatewayManagementApiClient.builder()
            .region(Region.US_EAST_1)
            .endpointOverride(apiEndpoint)
            .build()

        var connectionInfo: MutableMap<String, Any>? = null
        try {
            val getConnectionRequest = GetConnectionRequest.builder()
                .connectionId(connectionId)
                .build()
            val response = callbackAPI.getConnection(getConnectionRequest)
            if (response != null) {
                connectionInfo = mutableMapOf(
                    "connectionId" to (connectionId ?: "Unknown"),
                )
            }
        } catch (e: Exception) {
            println(e)
        }
        connectionInfo?.let {
            val connId = it["connectionId"] as? String
            connId?.let { connIdVal ->
                try {
                    val data = "Use the sendmessage route to send a message. Your info: ${it.toString()}"
                    val dataBytes = SdkBytes.fromUtf8String(data)
                    val postToConnectionRequest = PostToConnectionRequest.builder()
                        .connectionId(connIdVal)
                        .data(dataBytes)
                        .build()
                    callbackAPI.postToConnection(postToConnectionRequest)
                } catch (e: Exception) {
                    println(e)
                }
            }
        }
        return mapOf("statusCode" to 200)
    }
}
