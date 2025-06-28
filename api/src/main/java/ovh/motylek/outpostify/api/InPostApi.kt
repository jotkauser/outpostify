package ovh.motylek.outpostify.api

import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json
import ovh.motylek.outpostify.api.endpoints.InPostApiEndpoints
import ovh.motylek.outpostify.api.types.InPostRefreshRequest
import ovh.motylek.outpostify.api.types.InPostRefreshResponse

internal class InPostApi(
    val refreshToken: String,
    val accessToken: String
) {
    private val httpClient = InPostHttpClient()
    private val json = Json {
        ignoreUnknownKeys = true
    }

    suspend fun renewCredentials(): String {
        val response =  httpClient.post<InPostRefreshRequest>(
            InPostApiEndpoints.REFRESH_TOKEN,
            null, null,
            InPostRefreshRequest(refreshToken, "Android")
        )
        val tokens = json.decodeFromString<InPostRefreshResponse>(response.bodyAsText())
        return tokens.accessToken

    }
}