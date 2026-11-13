package com.example.lab7rickymorty.data.repository

import android.util.Log
import com.example.lab7rickymorty.data.remote.PageResponseDto
import kotlinx.coroutines.delay
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException
import java.time.Duration
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

private const val TAG = "RickMortySync"
private const val PAGE_DELAY_MS = 300L
private const val MAX_RETRIES_ON_429 = 3
private const val MAX_RETRY_WAIT_MS = 30_000L

// Descarga todos los registros de un recurso del API haciendo la menor cantidad de peticiones:
// 1) página 1, solo para conocer info.count
// 2) UN request con todos los ids: /recurso/1,2,...,count
// 3) si eso falla o llegan menos de count, se cae al método de páginas (más lento y con pausas)
internal suspend fun <T> fetchAllRecords(
    resource: String,
    firstPage: suspend () -> PageResponseDto<T>,
    byIds: suspend (ids: String) -> List<T>,
    nextPage: suspend (url: String) -> PageResponseDto<T>
): List<T> {
    val first = retryOn429("$resource página 1") { firstPage() }
    val count = first.info.count

    // Con un solo id el API devuelve un objeto y no un arreglo, por eso se exige count > 1
    if (count > 1) {
        val ids = (1..count).joinToString(",")
        val records = tryFetchByIds(resource, count) { byIds(ids) }
        if (records != null && records.size >= count) {
            return records
        }
        Log.d(TAG, "$resource: ids múltiples no trajo $count registros, se usan páginas")
    }

    return fetchByPages(resource, first, nextPage)
}

// Devuelve null si el request por ids falla: así el llamador puede usar las páginas.
// Solo se capturan errores de red/HTTP/JSON; cualquier otro error (y la cancelación) se propaga.
private suspend fun <T> tryFetchByIds(
    resource: String,
    count: Int,
    request: suspend () -> List<T>
): List<T>? {
    val label = "$resource ids 1..$count"
    return try {
        request()
    } catch (e: HttpException) {
        if (e.code() == 429) log429(label, e, attempt = 1)
        Log.d(TAG, "$label falló con HTTP ${e.code()}")
        null
    } catch (e: IOException) {
        Log.d(TAG, "$label falló: ${e.message}")
        null
    } catch (e: SerializationException) {
        Log.d(TAG, "$label devolvió un JSON inesperado: ${e.message}")
        null
    }
}

// Método de respaldo: sigue info.next con una pausa entre páginas para no disparar el límite
private suspend fun <T> fetchByPages(
    resource: String,
    first: PageResponseDto<T>,
    nextPage: suspend (url: String) -> PageResponseDto<T>
): List<T> {
    val all = first.results.toMutableList()
    var nextUrl = first.info.next
    while (nextUrl != null) {
        delay(PAGE_DELAY_MS)
        val url = nextUrl
        val page = retryOn429("$resource $url") { nextPage(url) }
        all += page.results
        nextUrl = page.info.next
    }
    return all
}

// Ejecuta el request y, si responde 429, reintenta hasta MAX_RETRIES_ON_429 veces.
// Cualquier otro error se propaga tal cual (el LoginViewModel muestra el mensaje).
private suspend fun <R> retryOn429(label: String, request: suspend () -> R): R {
    var retries = 0
    while (true) {
        try {
            return request()
        } catch (e: HttpException) {
            if (e.code() != 429) throw e
            val retryAfter = log429(label, e, attempt = retries + 1)
            if (retries >= MAX_RETRIES_ON_429) throw e
            retries++
            delay(retryWaitMillis(retryAfter, retries))
        }
    }
}

private fun log429(label: String, e: HttpException, attempt: Int): String? {
    val retryAfter = e.response()?.headers()?.get("Retry-After")
    Log.d(TAG, "HTTP 429 en \"$label\" (intento $attempt), Retry-After=${retryAfter ?: "no viene"}")
    return retryAfter
}

// Retry-After puede venir en segundos ("5") o como fecha HTTP. Si no viene o no se entiende,
// se espera 1 s, 2 s, 4 s... Siempre con un tope para que el login no quede colgado.
private fun retryWaitMillis(retryAfter: String?, retry: Int): Long {
    val fromHeader = retryAfter?.trim()?.let { value ->
        value.toLongOrNull()?.times(1000) ?: try {
            val date = ZonedDateTime.parse(value, DateTimeFormatter.RFC_1123_DATE_TIME)
            Duration.between(ZonedDateTime.now(date.zone), date).toMillis()
        } catch (e: DateTimeParseException) {
            null
        }
    }
    val wait = fromHeader ?: (1000L shl (retry - 1))
    return wait.coerceIn(0L, MAX_RETRY_WAIT_MS)
}
