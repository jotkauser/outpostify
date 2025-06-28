package ovh.motylek.outpostify.data.database

import androidx.room.TypeConverter
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format

class Converters {
    private val dateTimeFormatter = LocalDateTime.Formats.ISO

    @TypeConverter
    fun localDateTimeToString(value: LocalDateTime): String {
        return value.format(dateTimeFormatter)
    }

    @TypeConverter
    fun localDateTimeFromString(value: String): LocalDateTime {
        return LocalDateTime.parse(value, dateTimeFormatter)
    }
}