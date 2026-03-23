package ovh.motylek.outpostify.ui.routes

import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

sealed class Route {
    @Serializable
    object Welcome : Route()
    @Serializable
    object AddAccount : Route()
    @Serializable
    data object Parcels : Route()
}


val routesSerializer = SerializersModule {
    polymorphic(Route::class) {
        subclass(Route.Welcome::class, Route.Welcome.serializer())
        subclass(Route.AddAccount::class, Route.AddAccount.serializer())
        subclass(Route.Parcels::class, Route.Parcels.serializer())
    }
}