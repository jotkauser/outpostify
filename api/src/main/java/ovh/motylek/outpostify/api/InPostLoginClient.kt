package ovh.motylek.outpostify.api

import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json
import ovh.motylek.outpostify.api.endpoints.InPostApiEndpoints
import ovh.motylek.outpostify.api.errors.InvalidCodeException
import ovh.motylek.outpostify.api.types.InPostSmsCodeRequest
import ovh.motylek.outpostify.api.types.InPostSmsRequest
import ovh.motylek.outpostify.api.types.InPostTokenResponse

class InPostLoginClient(
    androidVersion: String,
    deviceModel: String,
    deviceManufacturer: String,
    deviceCodename: String
) {
    private val httpClient = InPostHttpClient(androidVersion, deviceModel, deviceManufacturer, deviceCodename)

    suspend fun requestSms(phoneNumber: String) {
        httpClient.post<InPostSmsRequest>(
            InPostApiEndpoints.REQUEST_SMS,
            null,
            null,
            InPostSmsRequest(phoneNumber = InPostSmsRequest.InPostSmsRequestNumber("+48", phoneNumber))
        )
    }

    suspend fun getTokens(phoneNumber: String, smsCode: String): InPostTokenResponse {
        val response = httpClient.post<InPostSmsCodeRequest>(
            InPostApiEndpoints.GET_TOKENS,
            null, null,
            InPostSmsCodeRequest(phoneNumber = InPostSmsRequest.InPostSmsRequestNumber("+48", phoneNumber),  smsCode, "Android")
        )
        if (response.status.value == 403) {
            throw InvalidCodeException()
        }
        val tokens = Json.decodeFromString<InPostTokenResponse>(response.bodyAsText())
        return tokens
    }
}