package com.pedroaba.tccmobile.backend.http

import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode

class BackendApiException(
    message: String,
    val statusCode: Int? = null
) : Exception(message)

suspend inline fun <reified T> safeApiCall(crossinline block: suspend () -> T): Result<T> = runCatching {
    try {
        block()
    } catch (error: ClientRequestException) {
        val status = error.response.status
        val body = error.response.bodyAsText()
        throw BackendApiException(
            message = clientErrorMessage(status, body),
            statusCode = status.value
        )
    } catch (error: ServerResponseException) {
        val status = error.response.status
        throw BackendApiException(
            message = error.response.bodyAsText().ifBlank { "Backend indisponivel no momento" },
            statusCode = status.value
        )
    }
}

fun HttpRequestBuilder.bearerAuth(token: String) {
    val normalizedToken = token.removePrefix("Bearer ").removePrefix("bearer ").trim()
    headers.append(HttpHeaders.Authorization, "Bearer $normalizedToken")
}

fun Throwable.isBackendAuthFailure(): Boolean =
    this is BackendApiException && statusCode in listOf(
        HttpStatusCode.Unauthorized.value,
        HttpStatusCode.Forbidden.value
    )

@PublishedApi
internal fun clientErrorMessage(status: HttpStatusCode, body: String): String {
    if (status == HttpStatusCode.Unauthorized || status == HttpStatusCode.Forbidden) {
        return body.ifBlank { "Sessao expirada. Faca login novamente." }
    }

    return body.ifBlank { "Requisicao invalida ao backend (${status.value})." }
}
