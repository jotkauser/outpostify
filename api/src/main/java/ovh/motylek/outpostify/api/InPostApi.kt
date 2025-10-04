package ovh.motylek.outpostify.api

import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json
import ovh.motylek.outpostify.api.endpoints.InPostApiEndpoints
import ovh.motylek.outpostify.api.types.InPostParcel
import ovh.motylek.outpostify.api.types.InPostParcelHeader
import ovh.motylek.outpostify.api.types.InPostRefreshRequest
import ovh.motylek.outpostify.api.types.InPostRefreshResponse

internal class InPostApi(
    val refreshToken: String,
    val accessToken: String,

    androidVersion: String,
    deviceModel: String,
    deviceManufacturer: String,
    deviceCodename: String
) {
    private val httpClient = InPostHttpClient(androidVersion, deviceModel, deviceManufacturer, deviceCodename)
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

    suspend fun getTrackedParcels(): List<InPostParcel> {
        val response = httpClient.get(
            InPostApiEndpoints.TRACKED_PARCELS,
            null, mapOf("Authorization" to accessToken)
        )
        val parcels = json.decodeFromString<InPostParcelHeader>(response.bodyAsText())
        return parcels.parcels
    }

    suspend fun getSentParcels() {

    }

    suspend fun getReturnedParcels() {

    }
}