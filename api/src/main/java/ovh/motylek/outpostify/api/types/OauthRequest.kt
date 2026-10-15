package ovh.motylek.outpostify.api.types

data class InPostOAuthRequest(
    val url: String,
    val verifier: String,
    val state: String
)