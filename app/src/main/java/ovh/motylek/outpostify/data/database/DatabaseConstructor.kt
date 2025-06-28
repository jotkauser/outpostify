package ovh.motylek.outpostify.data.database

import android.content.Context
import androidx.room.Room

fun createDatabase(context: Context): OutPostifyDatabase {
    val appContext = context.applicationContext
    val file = appContext.getDatabasePath("outpostify.db")
    return Room.databaseBuilder(
        context = appContext,
        klass = OutPostifyDatabase::class.java,
        name = file.absolutePath
    )
        .allowMainThreadQueries()
        .build()
}