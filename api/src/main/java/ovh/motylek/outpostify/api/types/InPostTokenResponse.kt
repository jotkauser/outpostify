package ovh.motylek.outpostify.api.types

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InPostTokenResponse(
    val refreshToken: String,
    @SerialName("authToken")
    val accessToken: String
)
