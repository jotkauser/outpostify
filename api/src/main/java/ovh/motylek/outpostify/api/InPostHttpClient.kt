package ovh.motylek.outpostify.api

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.URLBuilder
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import ovh.motylek.outpostify.api.endpoints.InPostApiEndpoints

internal class InPostHttpClient(
    val androidVersion: String,
    val deviceModel: String,
    val deviceManufacturer: String,
    val deviceCodename: String
) {

    val httpClient = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            Json {
                ignoreUnknownKeys = true
                isLenient = true
            }
        }
        install(UserAgent) {
            agent = "InPost-Mobile/3.41.1(34101000) (Android $androidVersion; $deviceModel; Xiaomi $deviceCodename; en)"
        }
    }

    suspend fun testClient() {
        httpClient.get("https://motylek.ovh")
    }

    suspend fun get(url: String, query: Map<String, String>?, headers: Map<String, String>?): HttpResponse {
        val urlBuilder = URLBuilder("${InPostApiEndpoints.BASE_URL}$url")
        query?.forEach {
            urlBuilder.parameters.append(it.key, it.value)
        }

        val response = httpClient.get(urlBuilder.buildString()) {
            headers {
                headers?.forEach {
                    append(it.key, it.value)
                }
            }
        }

        return response
    }

    suspend inline fun <reified V> post(url: String, query: Map<String, String>?, headers: Map<String, String>?, body: V): HttpResponse {
        val urlBuilder = URLBuilder("${InPostApiEndpoints.BASE_URL}$url")
        query?.forEach {
            urlBuilder.parameters.append(it.key, it.value)
        }

        val response = httpClient.post(urlBuilder.buildString()) {
            headers {
                headers?.forEach {
                    append(it.key, it.value)
                }
            }
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(body))
        }

        return response
    }

}