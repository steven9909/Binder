import Result.Companion.loading
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.MainThread
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import timber.log.Timber

@SuppressWarnings("TooGenericExceptionCaught")
inline fun catchNonFatal(block: () -> Unit) {
    try {
        block()
    } catch (e: Exception) {
        Timber.e(e, "catchNonFatal caught error")
    }
}

@SuppressWarnings("TooGenericExceptionCaught")
inline fun <T> resultCatching(block: () -> T): Result<T> {
    return try {
        Result.success(block())
    } catch (e: Exception) {
        Result.error(null, e)
    }
}

fun <T: Result<Any>> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    observe(lifecycleOwner, object : Observer<T> {
        override fun onChanged(t: T?) {
            observer.onChanged(t)
            if (t?.status == Status.ERROR || t?.status == Status.SUCCESS) {
                removeObserver(this)
            }
        }
    })
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

fun View.changeBackgroundColor(@ColorInt color: Int) {
    DrawableCompat.setTint(DrawableCompat.wrap(this.background), color)
}

fun View.setVisibility(isVisible: Boolean) {
    this.visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
}

inline fun <reified T> List<*>?.castToList(): List<T> {
    return this?.filterIsInstance<T>().takeIf { it?.size == this?.size } ?: emptyList()
}
