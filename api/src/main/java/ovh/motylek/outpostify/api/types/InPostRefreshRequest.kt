package ovh.motylek.outpostify.api.types

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InPostRefreshRequest(
    val refreshToken: String,
    @SerialName("phoneOS")
    val phoneOs: String
)

@Serializable
data class InPostRefreshResponse(
    @SerialName("authToken")
    val accessToken: String,
)