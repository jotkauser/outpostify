package ovh.motylek.outpostify.api

import ovh.motylek.outpostify.api.endpoints.InPostApiEndpoints
import ovh.motylek.outpostify.api.errors.InvalidCodeException
import ovh.motylek.outpostify.api.types.InPostSmsCodeRequest
import ovh.motylek.outpostify.api.types.InPostSmsRequest

class InPostLoginClient {
    private val httpClient = InPostHttpClient()

    suspend fun requestSms(phoneNumber: String) {
        httpClient.post<InPostSmsRequest>(
            InPostApiEndpoints.REQUEST_SMS,
            null,
            null,
            InPostSmsRequest(phoneNumber = InPostSmsRequest.InPostSmsRequestNumber("+48", phoneNumber))
        )
    }

    suspend fun getTokens(phoneNumber: String, smsCode: String) {
        val response = httpClient.post<InPostSmsCodeRequest>(
            InPostApiEndpoints.GET_TOKENS,
            null, null,
            InPostSmsCodeRequest(phoneNumber = InPostSmsRequest.InPostSmsRequestNumber("+48", phoneNumber),  smsCode, "Android")
        )
        if (response.status.value == 403) {
            throw InvalidCodeException()
        }
    }
}