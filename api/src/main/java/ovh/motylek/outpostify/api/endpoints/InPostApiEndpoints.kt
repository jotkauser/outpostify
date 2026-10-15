package ovh.motylek.outpostify.api.endpoints

object InPostApiEndpoints {
    const val BASE_URL = "https://api-inmobile-pl.easypack24.net/"
    const val OAUTH2_BASE_URL = "https://account.inpost-group.com/"
    const val OAUTH2_CLIENT_ID = "inpost-mobile"
    const val OAUTH2_AUTHORIZE = "oauth2/authorize"
    const val OAUTH2_CALLBACK = "callback"
    const val OAUTH2_GET_TOKENS = "global/oauth2/token"
    const val TRACKED_PARCELS = "v4/parcels/tracked"
    const val SENT_PARCELS = "v4/parcels/sent"
}