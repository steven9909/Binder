package viewmodel

import com.example.binder.ui.usecase.GetRecordingUseCase

class ChatMoreOptionsBottomSheetViewModel (
    private val getRecordingUseCase: GetRecordingUseCase<String>
    ) :  BaseViewModel() {
            fun setRoomName(roomName: String) {
                getRecordingUseCase.setParameter(roomName)
            }

            fun getRecordings() = getRecordingUseCase.getData()
    }

