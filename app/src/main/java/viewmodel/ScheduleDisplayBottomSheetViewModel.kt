package viewmodel

import Result.Companion.loading
import androidx.lifecycle.liveData
import com.example.binder.ui.usecase.DeleteScheduleUseCase
import kotlinx.coroutines.Dispatchers
import repository.FirebaseRepository

class ScheduleDisplayBottomSheetViewModel(val deleteScheduleUseCase: DeleteScheduleUseCase):
    BaseViewModel() {

    fun deleteEvent(uid: String, cid: String) {
        deleteScheduleUseCase.setParameter(Pair(uid, cid))
    }

    fun getScheduleToDelete() = deleteScheduleUseCase.getData()

}
