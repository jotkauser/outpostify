package ovh.motylek.outpostify.api

import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json
import ovh.motylek.outpostify.api.endpoints.InPostApiEndpoints
import ovh.motylek.outpostify.api.types.InPostParcel
import ovh.motylek.outpostify.api.types.InPostParcelHeader
import ovh.motylek.outpostify.api.types.InPostRefreshTokenResponse

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

    suspend fun renewCredentials(): InPostRefreshTokenResponse {
        val params = mapOf(
            "client_id" to InPostApiEndpoints.OAUTH2_CLIENT_ID,
            "grant_type" to "refresh_token",
            "refresh_token" to refreshToken
        )
        val response = httpClient.postForm(
            url = "${InPostApiEndpoints.BASE_URL}${InPostApiEndpoints.OAUTH2_GET_TOKENS}",
            formParameters = params
        )
        return json.decodeFromString<InPostRefreshTokenResponse>(response.bodyAsText())

    }

    suspend fun getTrackedParcels(): List<InPostParcel> {
        val response = httpClient.get(
            InPostApiEndpoints.TRACKED_PARCELS,
            null, mapOf("Authorization" to "Bearer $accessToken")
        )
        val parcels = json.decodeFromString<InPostParcelHeader>(response.bodyAsText())
        return parcels.parcels
    }

    suspend fun getSentParcels(): List<InPostParcel> {
        val response = httpClient.get(
            InPostApiEndpoints.SENT_PARCELS,
            null, mapOf("Authorization" to "Bearer $accessToken")
        )
        val parcels = json.decodeFromString<InPostParcelHeader>(response.bodyAsText())
        return parcels.parcels
    }

    suspend fun getReturnedParcels() {

    }
}