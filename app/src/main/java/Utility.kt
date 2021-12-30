import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

inline fun catchNonFatal(block: () -> Unit) {
    try {
        block()
    } catch (e: Exception) {
        Timber.e(e, "catchNonFatal caught error")
    }
}
