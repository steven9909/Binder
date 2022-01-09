package data

import com.google.firebase.Timestamp

sealed class BaseData {
    open val uid: String? = null
}

data class User(val school:String,
                val program:String,
                val interests:String,
                val name:String?=null,
                val token:String?=null,
                val userGroups:List<Group>?=null): BaseData() {
    constructor(): this("", "", "", "", null, null)
}

data class Settings(val enableNotifications:Boolean=true,
                    val language:String="English",
                    val customStatus:String="Active"): BaseData() {
    constructor(): this(true, "", "")
}

data class Friend(val friendId:String): BaseData() {
    constructor(): this("")
}

data class FriendRequest(val requesterId:String?,
                         val receivingId:String?): BaseData() {
    constructor(): this("", "")
}

data class CalendarEvent(val name:String,
                         val startTime:Timestamp,
                         val endTime:Timestamp,
                         val allDay:Boolean=false,
                         val recurringEvent:String?=null,
                         val minutesBefore:Long=defaultMinutes): BaseData() {
    companion object {
        private const val defaultMinutes = 15.toLong()
    }
    constructor(): this("", Timestamp.now(), Timestamp.now(), false, null, defaultMinutes)
}

data class Group(val groupName:String,
                 val members:List<String>): BaseData() {
    constructor(): this("", emptyList())
}

data class Message(val sendingId:String,
                   val receivingId:String,
                   val msg:String,
                   val sentTime:Timestamp,
                   val read:Boolean=false): BaseData() {
    constructor() : this("", "", "", Timestamp.now(), false)
}
