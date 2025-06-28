package ovh.motylek.outpostify.api.types

import kotlinx.serialization.Serializable

@Serializable
data class InPostJwt(
    val exp: Long,
)
