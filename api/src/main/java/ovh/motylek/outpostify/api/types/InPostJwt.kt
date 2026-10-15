package ovh.motylek.outpostify.api.types

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InPostJwt(
    val exp: Long,
    val phone: String,
    @SerialName("phone_prefix")
    val phonePrefix: String
)
