package ovh.motylek.outpostify.di

import org.koin.android.ext.koin.androidContext
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.ksp.generated.module
import ovh.motylek.outpostify.data.database.OutPostifyDatabase
import ovh.motylek.outpostify.data.database.createDatabase
import ovh.motylek.outpostify.data.repository.AccountRepository


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
}

val repositoryModule = module {
    singleOf(::AccountRepository)
}

val appModules = arrayOf(
    ViewModelsModule().module,
    databaseModule,
    daoModule,
    repositoryModule
)