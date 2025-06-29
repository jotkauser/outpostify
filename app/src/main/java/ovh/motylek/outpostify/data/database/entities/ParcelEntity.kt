package ovh.motylek.outpostify.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import ovh.motylek.outpostify.api.data.ParcelShipmentType
import ovh.motylek.outpostify.api.data.ParcelType

@Suppress("PropertyName")
@Entity(tableName = "parcels")
data class ParcelEntity(
    @PrimaryKey
    val shipmentNumber: String,
    val shipmentType: ParcelShipmentType,
    val senderName: String?,
    val type: ParcelType,
    val pickupData_name: String?,
    val pickupData_latitude: Double?,
    val pickupData_longitude: Double?,
    val pickupData_city: String?,
    val pickupData_address: String?,
    val pickupData_openCode: String?,
    val pickupData_openQrCode: String?,

    val userId: Long
)