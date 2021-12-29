import timber.log.Timber

inline fun catchNonFatal(block: () -> Unit) {
    try {
        block()
    } catch (e: Exception) {
        Timber.e(e, "catchNonFatal caught error")
    }
}