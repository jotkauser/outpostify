package ovh.motylek.outpostify.data.database.mappers

import ovh.motylek.outpostify.api.data.Parcel
import ovh.motylek.outpostify.api.data.ParcelEvent
import ovh.motylek.outpostify.api.data.ParcelPickupData
import ovh.motylek.outpostify.data.database.entities.ParcelEntity
import ovh.motylek.outpostify.data.database.entities.ParcelEventEntity
import ovh.motylek.outpostify.data.database.entities.ParcelWithEvents

fun List<Parcel>.mapToEntities(userId: Long): Pair<List<ParcelEntity>, List<ParcelEventEntity>> {
    val parcelEntities = this.map {
        ParcelEntity(
            type = it.type,
            shipmentNumber = it.shipmentNumber,
            shipmentType = it.shipmentType,
            senderName = it.senderName,
            pickupData_city = it.pickupData?.city,
            pickupData_address = it.pickupData?.address,
            pickupData_latitude = it.pickupData?.latitude,
            pickupData_longitude = it.pickupData?.longitude,
            pickupData_name = it.pickupData?.name,
            pickupData_openCode = it.pickupData?.lockerOpenCode,
            pickupData_openQrCode = it.pickupData?.lockerQrCode,
            userId = userId
        )
    }
    val parcelEventEntities = this.flatMap { parcel ->
        parcel.events.map {
            ParcelEventEntity(
                shipmentNumber = parcel.shipmentNumber,
                date = it.date,
                type = it.status,
            )
        }
    }
    return Pair(parcelEntities, parcelEventEntities)
}

fun List<ParcelWithEvents>.mapToParcels(userId: Long): List<Parcel> {
    return this.filter { it.parcel.userId == userId }.map {
        Parcel(
            shipmentNumber = it.parcel.shipmentNumber,
            shipmentType = it.parcel.shipmentType,
            senderName = it.parcel.senderName,
            type = it.parcel.type,
            pickupData = ParcelPickupData(
                name = it.parcel.pickupData_name,
                lockerQrCode = it.parcel.pickupData_openQrCode,
                lockerOpenCode = it.parcel.pickupData_openCode,
                latitude = it.parcel.pickupData_latitude,
                longitude = it.parcel.pickupData_longitude,
                address = "${it.parcel.pickupData_address} ${it.parcel.pickupData_city}",
                city = it.parcel.pickupData_city,
            ),
            events = it.events.map {
                ParcelEvent(
                    date = it.date,
                    status = it.type
                )
            }
        )
    }
}