package ovh.motylek.outpostify.di

import org.koin.android.ext.koin.androidContext
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.plugin.module.dsl.single
import ovh.motylek.outpostify.data.database.OutPostifyDatabase
import ovh.motylek.outpostify.data.database.createDatabase
import ovh.motylek.outpostify.data.repository.AccountRepository
import ovh.motylek.outpostify.data.repository.ParcelRepository
import ovh.motylek.outpostify.service.ClientManager

@Module
@ComponentScan("ovh.motylek.outpostify.ui")
class ViewModelsModule()

val databaseModule = module {
    single<OutPostifyDatabase> {
        createDatabase(androidContext())
    }
}

val daoModule = module {
    single { get<OutPostifyDatabase>().accountDao() }
    single { get<OutPostifyDatabase>().parcelDao() }
}

val repositoryModule = module {
    single<ParcelRepository>()
    single<AccountRepository>()
}

val appCoreModule = module {
    single<ClientManager>()
}

val appModules = arrayOf(
    databaseModule,
    daoModule,
    repositoryModule,
    appCoreModule
)