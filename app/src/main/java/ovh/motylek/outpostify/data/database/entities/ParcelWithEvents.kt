package ovh.motylek.outpostify.data.database.entities

import androidx.room3.Embedded
import androidx.room3.Relation

data class ParcelWithEvents(
    @Embedded val parcel: ParcelEntity,
    @Relation(
        parentColumns = ["shipmentNumber"],
        entityColumns = ["shipmentNumber"]
    )
    val events: List<ParcelEventEntity>
)
