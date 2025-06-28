package ovh.motylek.outpostify.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.ksp.generated.module


@Module
@ComponentScan("ovh.motylek.outpostify.ui")
class ViewModelsModule()

val appModules = arrayOf(
    ViewModelsModule().module
)