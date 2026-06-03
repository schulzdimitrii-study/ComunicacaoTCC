package com.pedroaba.tccmobile.backend.http

import io.ktor.http.HttpStatusCode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BackendRequestTest {

    @Test
    fun `clientErrorMessage returns auth message for unauthorized without body`() {
        val message = clientErrorMessage(HttpStatusCode.Unauthorized, "")

        assertEquals("Sessao expirada. Faca login novamente.", message)
    }

    @Test
    fun `clientErrorMessage preserves backend body for bad request`() {
        val message = clientErrorMessage(HttpStatusCode.BadRequest, "hordeId invalido")

        assertEquals("hordeId invalido", message)
    }

    @Test
    fun `clientErrorMessage includes status for empty bad request`() {
        val message = clientErrorMessage(HttpStatusCode.BadRequest, "")

        assertEquals("Requisicao invalida ao backend (400).", message)
    }

    @Test
    fun `isBackendAuthFailure is true for forbidden`() {
        val error = BackendApiException("forbidden", HttpStatusCode.Forbidden.value)

        assertTrue(error.isBackendAuthFailure())
    }

    @Test
    fun `isBackendAuthFailure is false for validation error`() {
        val error = BackendApiException("bad request", HttpStatusCode.BadRequest.value)

        assertFalse(error.isBackendAuthFailure())
    }
}
