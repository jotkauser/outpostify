package ovh.motylek.outpostify.data.database.entities

import androidx.room.Embedded
import androidx.room.Relation

data class ParcelWithEvents(
    @Embedded val parcel: ParcelEntity,
    @Relation(
        parentColumn = "shipmentNumber",
        entityColumn = "shipmentNumber"
    )
    val events: List<ParcelEventEntity>
)
