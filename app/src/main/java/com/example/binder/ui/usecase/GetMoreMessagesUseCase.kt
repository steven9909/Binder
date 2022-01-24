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
import data.Question
import repository.GetMessageFailException

class GetMoreMessagesUseCase(private val realtimeDB: RealtimeDB) :
    BaseUseCase<Pair<String, Long>, Result<List<Message>>>() {

    override val parameter: MutableLiveData<Pair<String, Long>> = MutableLiveData()

    override val liveData: LiveData<Result<List<Message>>> = parameter.switchMap {
        val data: MutableLiveData<Result<List<Message>>> = MutableLiveData(Result.loading(null))
        val valueEventListener = object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                data.postValue(Result.error(null, GetMessageFailException))
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Message>()
                for (child in snapshot.children) {
                    (child.value as? Map<String, *>)?.let { d ->
                        if (d["question"] != null) {
                            list.add(
                                Message(
                                    d["sendingId"] as String,
                                    d["msg"] as String,
                                    d["timestamp"] as Long,
                                    d["fileLink"] as? String?,
                                    Question(
                                        ((d["question"]) as HashMap<String, *>)["question"] as String,
                                        ((d["question"]) as HashMap<String, *>)["answers"] as List<String>,
                                        ((d["question"]) as HashMap<String, *>)["answerIndexes"] as List<Int>)
                                )
                            )
                        } else {
                            list.add(
                                Message(
                                    d["sendingId"] as String,
                                    d["msg"] as String,
                                    d["timestamp"] as Long,
                                    d["fileLink"] as? String?,
                                    null
                                )
                            )
                        }
                    }
                }
                data.postValue(Result.success(list))
            }
        }
        realtimeDB.getMoreMessages(it.first, it.second, valueEventListener)
        data
    }
}
