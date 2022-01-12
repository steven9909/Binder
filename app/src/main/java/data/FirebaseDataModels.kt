package data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude

sealed class BaseData {
    open val uid: String? = null
}

data class User(val school:String?,
                val program:String?,
                val interests:String?,
                val name:String?=null,
                val token:String?=null,
                val userGroups:List<String>,
                @get:Exclude override val uid: String?=null): BaseData() {
    constructor(): this("", "", "", null, null, emptyList(), null)
}

data class Settings(val enableNotifications:Boolean=true,
                    val language:String="English",
                    val customStatus:String="Active",
                    @get:Exclude override val uid: String?=null): BaseData() {
    constructor(): this(true, "", "", null)
}

data class Friend(override val uid: String?=null): BaseData() {
    constructor(): this(null)
}

data class FriendRequest(val requesterId:String?,
                         val receivingId:String?,
                         @get:Exclude override val uid: String?=null): BaseData() {
    constructor(): this("", "", null)
}

data class CalendarEvent(val name:String,
                         val startTime:Timestamp,
                         val endTime:Timestamp,
                         val allDay:Boolean=false,
                         val recurringEvent:String?=null,
                         val minutesBefore:Long=defaultMinutes,
                         @get:Exclude override val uid: String?=null): BaseData() {
    companion object {
        private const val defaultMinutes = 15.toLong()
    }
    constructor(): this("", Timestamp.now(), Timestamp.now(), false, null, defaultMinutes, null)
}

data class Group(val groupName:String,
                 val members:List<String>,
                 @get:Exclude override val uid: String?=null): BaseData() {
    constructor(): this("", emptyList(), null)
}

data class Message(val sendingId:String,
                   val receivingId:String,
                   val msg:String,
                   val sentTime:Timestamp,
                   val read:Boolean=false,
                   @get:Exclude override val uid: String?=null): BaseData() {
    constructor() : this("", "", "", Timestamp.now(), false, null)
}
