package ovh.motylek.outpostify.api

import kotlin.time.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import ovh.motylek.outpostify.api.data.Parcel
import ovh.motylek.outpostify.api.data.ParcelType
import ovh.motylek.outpostify.api.mappers.mapParcels
import ovh.motylek.outpostify.api.utils.getJwtExpiration
import kotlin.time.ExperimentalTime

class InPostApiClient(
    val refreshToken: String,
    val accessToken: String,

    val androidVersion: String,
    val deviceModel: String,
    val deviceManufacturer: String,
    val deviceCodename: String
) {
    private val api = InPostApi(refreshToken, accessToken, androidVersion, deviceModel, deviceManufacturer, deviceCodename)

    @OptIn(ExperimentalTime::class)
    fun isRenewCredentialsNeeded(): Boolean {
        val tokenExpiration = getJwtExpiration(accessToken)
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        return tokenExpiration < now
    }

    suspend fun renewCredentials(): String = api.renewCredentials()

    suspend fun getTrackedParcels(): List<Parcel> = api.getTrackedParcels().mapParcels(ParcelType.RECEIVED)
}