package viewmodel

import androidx.lifecycle.viewModelScope
import data.Message
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import repository.RealtimeDB

class ChatFragmentViewModel(val realtimeDB: RealtimeDB) : BaseViewModel() {

    private val REFRESH_INTERVAL = 1000L

    private suspend fun messageGetterFlow(uid: String): Flow<Unit> = flow {
        delay(REFRESH_INTERVAL)
        emit(realtimeDB.getMessage(uid))
    }

    suspend fun messageCollector(uid: String) {
        viewModelScope.launch {
            messageGetterFlow(uid).collect()
        }
    }

    suspend fun messageSenderFlow(message: Message, uid: String): Flow<Unit> = flow {
        emit(realtimeDB.sendMessage(message, uid))
    }
}
