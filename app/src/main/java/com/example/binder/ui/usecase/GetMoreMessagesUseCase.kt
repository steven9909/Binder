package com.example.binder.ui.usecase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import repository.RealtimeDB
import Result
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import data.Message
import repository.GetMessageFailException

class GetMoreMessagesUseCase<T: Pair<String, Long>>(private val realtimeDB: RealtimeDB) : BaseUseCase<T, Result<List<Message>>>() {

    override val parameter: MutableLiveData<T> = MutableLiveData()

    override val liveData: LiveData<Result<List<Message>>> = parameter.switchMap {
        val data: MutableLiveData<Result<List<Message>>> = MutableLiveData(Result.loading(null))
        val valueEventListener = object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                data.postValue(Result.error(null, GetMessageFailException))
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                val ret = snapshot.value as? Map<*, *>
                ret?.let {
                    val list = mutableListOf<Message>()
                    for ((_, value) in ret) {
                        val map = value as? Map<String, *>
                        map?.let { t ->
                            list.add(Message(t["sendingId"] as String, t["msg"] as String, t["timestamp"] as Long, t["read"] as Boolean))
                        }
                    }
                    data.postValue(Result.success(list))
                }
            }
        }
        realtimeDB.getMoreMessages(it.first, it.second, valueEventListener)
        data
    }
}