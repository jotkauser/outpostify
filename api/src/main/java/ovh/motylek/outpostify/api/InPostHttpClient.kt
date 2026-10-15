package ovh.motylek.outpostify.api

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.URLBuilder
import io.ktor.http.contentType
import io.ktor.http.parameters
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
            agent = "InPost-Mobile/4.21.1(42101000) (Android $androidVersion; $deviceModel; $deviceManufacturer $deviceCodename; en)"
        }
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

    suspend fun postForm(
        url: String,
        formParameters: Map<String, String>,
        query: Map<String, String>? = null,
        headers: Map<String, String>? = null
    ): HttpResponse {
        val fullUrl = if (url.startsWith("http://") || url.startsWith("https://")) {
            url
        } else {
            "${InPostApiEndpoints.BASE_URL}$url"
        }

        val urlBuilder = URLBuilder(fullUrl)
        query?.forEach {
            urlBuilder.parameters.append(it.key, it.value)
        }

        return httpClient.submitForm(
            url = urlBuilder.buildString(),
            formParameters = parameters {
                formParameters.forEach { (key, value) ->
                    append(key, value)
                }
            }
        ) {
            headers {
                headers?.forEach {
                    append(it.key, it.value)
                }
            }
        }
    }

}