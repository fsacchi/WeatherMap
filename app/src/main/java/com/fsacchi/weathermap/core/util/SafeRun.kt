package com.fsacchi.weathermap.core.util

import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException

suspend fun <T> safeRun(block: suspend () -> T): Result<T> {
    return try {
        Result.Success(block())
    } catch (e: HttpException) {
        val message = when (e.code()) {
            401 -> "API key inválida ou não autorizada"
            404 -> "Cidade não encontrada"
            429 -> "Limite de requisições excedido"
            500 -> "Erro no servidor. Tente novamente."
            else -> "Erro HTTP ${e.code()}: ${e.message()}"
        }
        Timber.e(e)
        Result.Error(message, e)
    } catch (e: IOException) {
        Timber.e(e, "Network error")
        Result.Error("Sem conexão com a internet. Verifique sua rede.", e)
    } catch (e: Exception) {
        Timber.e(e, "Unexpected error")
        Result.Error(e.message ?: "Erro desconhecido", e)
    }
}
