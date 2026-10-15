package ovh.motylek.outpostify.api

import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json
import ovh.motylek.outpostify.api.types.InPostOAuthRequest
import ovh.motylek.outpostify.api.endpoints.InPostApiEndpoints
import ovh.motylek.outpostify.api.types.InPostCodeResponse
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.SecureRandom
import kotlin.io.encoding.Base64

class InPostLoginClient(
    androidVersion: String,
    deviceModel: String,
    deviceManufacturer: String,
    deviceCodename: String
) {
    private val httpClient = InPostHttpClient(androidVersion, deviceModel, deviceManufacturer, deviceCodename)
    private val secureRandom = SecureRandom()

    fun generateOAuthRequest(): InPostOAuthRequest {
        fun randomB64(bytesCount: Int): String {
            val bytes = ByteArray(bytesCount).also { secureRandom.nextBytes(it) }
            return Base64.UrlSafe.withPadding(Base64.PaddingOption.ABSENT).encode(bytes)
        }

        val verifier = randomB64(32)
        val state = randomB64(16)
        val nonce = randomB64(16)

        val digest = MessageDigest.getInstance("SHA-256")
        val challenge = Base64.UrlSafe.withPadding(Base64.PaddingOption.ABSENT)
            .encode(digest.digest(verifier.toByteArray(StandardCharsets.US_ASCII)))

        val params = mapOf(
            "response_type" to "code",
            "client_id" to InPostApiEndpoints.OAUTH2_CLIENT_ID,
            "redirect_uri" to "${InPostApiEndpoints.OAUTH2_BASE_URL}${InPostApiEndpoints.OAUTH2_CALLBACK}",
            "scope" to "openid",
            "code_challenge" to challenge,
            "code_challenge_method" to "S256",
            "state" to state,
            "nonce" to nonce,
            "response_mode" to "query"
        )

        val queryString = params.entries.joinToString("&") { (k, v) ->
            "${URLEncoder.encode(k, "UTF-8")}=${URLEncoder.encode(v, "UTF-8")}"
        }

        val url = "${InPostApiEndpoints.OAUTH2_BASE_URL}${InPostApiEndpoints.OAUTH2_AUTHORIZE}?$queryString"

        return InPostOAuthRequest(url = url, verifier = verifier, state = state)
    }

    suspend fun getTokens(code: String, state: String, request: InPostOAuthRequest): InPostCodeResponse {
        if (state != request.state) {
            throw IllegalStateException("state parameter doesn't match")
        }
        val params = mapOf(
            "grant_type" to "authorization_code",
            "client_id" to InPostApiEndpoints.OAUTH2_CLIENT_ID,
            "redirect_uri" to "${InPostApiEndpoints.OAUTH2_BASE_URL}${InPostApiEndpoints.OAUTH2_CALLBACK}",
            "code" to code,
            "code_verifier" to request.verifier
        )
        val response = httpClient.postForm(
            url = "${InPostApiEndpoints.BASE_URL}${InPostApiEndpoints.OAUTH2_GET_TOKENS}",
            formParameters = params
        )
        return Json.decodeFromString<InPostCodeResponse>(response.bodyAsText())
    }


}