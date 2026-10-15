package ovh.motylek.outpostify.api.utils

import kotlin.time.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.Json
import ovh.motylek.outpostify.api.types.InPostJwt
import kotlin.io.encoding.Base64
import kotlin.time.ExperimentalTime

val json = Json { ignoreUnknownKeys = true }

private fun decodeJwt(jwt: String): InPostJwt {
    val jwtParts = jwt.split(".")
    val payload = jwtParts[1]
    val b64Padding = Base64.UrlSafe.withPadding(Base64.PaddingOption.PRESENT_OPTIONAL)

    val decodedPayloadString = String(b64Padding.decode(payload))
    return json.decodeFromString<InPostJwt>(decodedPayloadString)
}

@OptIn(ExperimentalTime::class)
fun getJwtExpiration(jwt: String): LocalDateTime {
    val jsonObject = decodeJwt(jwt)
    val instant = Instant.fromEpochSeconds(jsonObject.exp)
    return instant.toLocalDateTime(TimeZone.currentSystemDefault())
}

fun getJwtPhoneNumber(jwt: String): String {
    val jsonObject = decodeJwt(jwt)
    return "${jsonObject.phonePrefix} ${jsonObject.phone}"
}