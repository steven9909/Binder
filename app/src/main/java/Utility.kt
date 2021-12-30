import com.google.android.gms.tasks.Task
import timber.log.Timber

inline fun catchNonFatal(block: () -> Unit) {
    try {
        block()
    } catch (e: Exception) {
        Timber.e(e, "catchNonFatal caught error")
    }
}

fun Task<Void>.toLiveData() {

}