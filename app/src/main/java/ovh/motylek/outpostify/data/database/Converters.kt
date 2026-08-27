package ovh.motylek.outpostify.data.database

import androidx.room3.ColumnTypeConverter
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format

class Converters {
    private val dateTimeFormatter = LocalDateTime.Formats.ISO

    @ColumnTypeConverter
    fun localDateTimeToString(value: LocalDateTime): String {
        return value.format(dateTimeFormatter)
    }

    @ColumnTypeConverter
    fun localDateTimeFromString(value: String): LocalDateTime {
        return LocalDateTime.parse(value, dateTimeFormatter)
    }
}