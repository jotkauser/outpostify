package ovh.motylek.outpostify.api.data

import kotlinx.datetime.LocalDateTime

enum class ParcelType {
    RECEIVED,
    SENT,
    RETURNED
}

enum class ParcelShipmentType {
    LOCKER,
    HOME
}

enum class ParcelStatus {
    CREATED,
    SENT,
    ADOPTED_AT_SOURCE_BRANCH,
    SENT_FROM_SOURCE_BRANCH,
    OUT_FOR_DELIVERY,
    READY_TO_PICKUP,
    DELIVERED,
    TAKEN_BY_COURIER,
    UNKNOWN
}

data class ParcelEvent(
    val status: ParcelStatus,
    val date: LocalDateTime
)

data class ParcelPickupData(
    val name: String?,
    val latitude: Double?,
    val longitude: Double?,
    val city: String?,
    val address: String?,
    val lockerOpenCode: String?,
    val lockerQrCode: String?
)



data class Parcel(
    val shipmentNumber: String,
    val shipmentType: ParcelShipmentType,
    val type: ParcelType,
    val pickupData: ParcelPickupData?,
    val events: List<ParcelEvent>,
    val senderName: String?,
)