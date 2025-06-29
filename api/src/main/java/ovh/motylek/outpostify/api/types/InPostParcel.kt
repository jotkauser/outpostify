package ovh.motylek.outpostify.api.types

import kotlinx.serialization.Serializable

@Serializable
data class InPostParcelHeader(
    val parcels: List<InPostParcel>
)

@Serializable
data class InPostParcel(
    val shipmentNumber: String,
    val shipmentType: String,
    val openCode: String? = null,
    val qrCode: String? = null,
    val pickUpPoint: InPostPickupPoint? = null,
    val eventLog: List<InPostEvent>,
    val sender: InPostSender? = null
) {
    @Serializable
    data class InPostPickupPoint(
        val name: String,
        val location: InPostLocation,
        val addressDetails: InPostAddress
    ) {
        @Serializable
        data class InPostLocation(
            val latitude: Double,
            val longitude: Double
        )

        @Serializable
        data class InPostAddress(
            val postCode: String,
            val city: String,
            val province: String,
            val street: String,
            val buildingNumber: String
        )
    }
    @Serializable
    data class InPostEvent(
        val type: String,
        val name: String,
        val date: String
    )

    @Serializable
    data class InPostSender(
        val name: String,
    )
}
