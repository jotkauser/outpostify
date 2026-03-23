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
    var refreshToken: String,
    var accessToken: String,

    val androidVersion: String,
    val deviceModel: String,
    val deviceManufacturer: String,
    val deviceCodename: String
) {
    private var api = createInPostApi()

    @OptIn(ExperimentalTime::class)
    fun isRenewCredentialsNeeded(): Boolean {
        val tokenExpiration = getJwtExpiration(accessToken)
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        return tokenExpiration < now
    }

    suspend fun renewCredentials(): String {
        val newToken = api.renewCredentials()
        this.accessToken = newToken
        this.api = createInPostApi()
        return newToken
    }

    suspend fun getTrackedParcels(): List<Parcel> = api.getTrackedParcels().mapParcels(ParcelType.RECEIVED)

    private fun createInPostApi() = InPostApi(refreshToken, accessToken, androidVersion, deviceModel, deviceManufacturer, deviceCodename)
}