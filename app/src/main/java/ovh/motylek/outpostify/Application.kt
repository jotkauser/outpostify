package ovh.motylek.outpostify

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.plugin.module.dsl.module
import ovh.motylek.outpostify.di.ViewModelsModule
import ovh.motylek.outpostify.di.appModules

class Application : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@Application)
            androidLogger()
            modules(*appModules)
            module<ViewModelsModule>()
        }
    }
}