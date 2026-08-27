package ovh.motylek.outpostify.data.database.entities

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import kotlinx.datetime.LocalDateTime

@Entity(tableName = "accounts")
data class AccountEntity(
    val accessToken: String,
    val refreshToken: String,
    val tokenExpiration: LocalDateTime,
    val phoneNumber: String,
    val selected: Boolean = false
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
