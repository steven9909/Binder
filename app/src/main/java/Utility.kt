import timber.log.Timber

inline fun catchNonFatal(block: () -> Unit) {
    try {
        block()
    } catch (e: Exception) {
        Timber.e(e, "catchNonFatal caught error")
    }
}

inline fun <T> resultCatching(block: () -> T): Result<T> {
    return try {
        Result.success(block())
    } catch (e: Exception) {
        Result.error(null, e)
    }
}

enum class Status {
    SUCCESS,
    ERROR,
    LOADING
}

data class Result<out T>(val status: Status, val data: T?, val exception: Exception?, val message: String?) {
    companion object {
        fun <T> success(data: T): Result<T> =
            Result(status = Status.SUCCESS, data = data, exception = null, message = null)

        fun <T> error(data: T?, e: Exception?, message: String? = null): Result<T> =
            Result(status = Status.ERROR, data = data, exception = e, message = message)

        fun <T> loading(data: T?): Result<T> =
            Result(status = Status.LOADING, data = data, exception = null, message = null)
    }
}
