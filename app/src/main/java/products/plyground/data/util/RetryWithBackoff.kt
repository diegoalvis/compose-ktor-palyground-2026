package products.plyground.data.util

import kotlinx.coroutines.delay
import kotlin.math.pow

suspend inline fun <T> retryWithBackoff(
    maxAttempts: Int = 3,
    initialDelayMs: Long = 1_000L,
    factor: Double = 2.0,
    crossinline block: suspend () -> T
): T {
    var lastException: Exception? = null
    repeat(maxAttempts) { attempt ->
        try {
            return block()
        } catch (e: Exception) {
            lastException = e
            val delayMs = (initialDelayMs * factor.pow(attempt.toDouble())).toLong()
            delay(delayMs)
        }
    }
    throw lastException!!
}
