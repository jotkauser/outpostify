package ovh.motylek.outpostify.ui.routes

import kotlinx.serialization.Serializable

@Serializable
object Welcome

@Serializable
object AddAccount

@Serializable
data class Parcels(val userId: Long)