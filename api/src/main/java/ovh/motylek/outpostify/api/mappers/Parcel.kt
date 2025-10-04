package ovh.motylek.outpostify.api.mappers

import kotlin.time.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import ovh.motylek.outpostify.api.data.Parcel
import ovh.motylek.outpostify.api.data.ParcelEvent
import ovh.motylek.outpostify.api.data.ParcelPickupData
import ovh.motylek.outpostify.api.data.ParcelShipmentType
import ovh.motylek.outpostify.api.data.ParcelStatus
import ovh.motylek.outpostify.api.data.ParcelType
import ovh.motylek.outpostify.api.types.InPostParcel
import kotlin.time.ExperimentalTime

fun List<InPostParcel>.mapParcels(type: ParcelType): List<Parcel> = map {
    Parcel(
        shipmentNumber = it.shipmentNumber,
        shipmentType = when (it.shipmentType) {
            "courier" -> ParcelShipmentType.HOME
            "parcel" -> ParcelShipmentType.LOCKER
            else -> ParcelShipmentType.HOME
        },
        senderName = it.sender?.name,
        type = type,
        pickupData = if (it.pickUpPoint != null) {
            ParcelPickupData(
                name = it.pickUpPoint.name,
                lockerQrCode = it.qrCode,
                lockerOpenCode = it.openCode,
                latitude = it.pickUpPoint.location.latitude,
                longitude = it.pickUpPoint.location.longitude,
                address = "${ it.pickUpPoint.addressDetails.street} ${ it.pickUpPoint.addressDetails.buildingNumber}",
                city = it.pickUpPoint.addressDetails.city
            )
        } else { null },
        events = it.eventLog.map { p ->
            ParcelEvent(
                date = parseTime(p.date),
                status = when (p.name) {
                    "CONFIRMED" -> ParcelStatus.CREATED
                    "DISPATCHED_BY_SENDER" -> ParcelStatus.SENT
                    "TAKEN_BY_COURIER" -> ParcelStatus.TAKEN_BY_COURIER
                    "ADOPTED_AT_SOURCE_BRANCH" -> ParcelStatus.ADOPTED_AT_SOURCE_BRANCH
                    "SENT_FROM_SOURCE_BRANCH" -> ParcelStatus.SENT_FROM_SOURCE_BRANCH
                    "OUT_FOR_DELIVERY" -> ParcelStatus.OUT_FOR_DELIVERY
                    "READY_TO_PICKUP" -> ParcelStatus.READY_TO_PICKUP
                    "DELIVERED" -> ParcelStatus.DELIVERED
                    else -> ParcelStatus.UNKNOWN
                }
            )
        }
    )
}

@OptIn(ExperimentalTime::class)
private fun parseTime(string: String): LocalDateTime {
    val instant = Instant.parse(string)
    val timezone = TimeZone.of("Europe/Warsaw")
    return instant.toLocalDateTime(timezone)
}