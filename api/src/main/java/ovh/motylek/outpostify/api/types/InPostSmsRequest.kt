package ovh.motylek.outpostify.api.types

import kotlinx.serialization.Serializable

@Serializable
data class InPostSmsRequest(
    val phoneNumber: InPostSmsRequestNumber
) {
    @Serializable
    data class InPostSmsRequestNumber(
        val prefix: String,
        val value: String
    )
}

@Serializable
data class InPostSmsCodeRequest(
    val phoneNumber: InPostSmsRequest.InPostSmsRequestNumber,
    val smsCode: String,
    val devicePlatform: String
)