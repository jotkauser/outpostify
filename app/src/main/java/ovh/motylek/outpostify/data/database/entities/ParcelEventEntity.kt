package ovh.motylek.outpostify.data.database.entities

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.PrimaryKey
import kotlinx.datetime.LocalDateTime
import ovh.motylek.outpostify.api.data.ParcelStatus

@Entity(
    tableName = "parcel_events",
    foreignKeys = [ForeignKey(
        entity = ParcelEntity::class,
        parentColumns = ["shipmentNumber"],
        childColumns = ["shipmentNumber"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class ParcelEventEntity(
    val shipmentNumber: String,
    val type: ParcelStatus,
    val date: LocalDateTime
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
