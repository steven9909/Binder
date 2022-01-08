package data

import com.google.firebase.Timestamp

data class User(val userId:String,
                val school:String,
                val program:String,
                val interests:String,
                val name:String?=null,
                val token:String?=null,
                val guid:Set<String>?=null) {
    constructor(): this("", "", "", "", null, null, null)
}

data class Settings(val enableNotifications:Boolean=true,
                    val language:String="English",
                    val customStatus:String="Active") {
    constructor(): this(true, "", "")
}

data class Friend(val friendId:String,
                  val myName:String,
                  val uid:String?=null) {
    constructor(): this("", "", null)
}

data class FriendRequest(val requesterId:String,
                         val receivingId: String,
                         val uid: String?=null) {
    constructor(): this("", "", null)
}

data class CalendarEvent(val name:String,
                         val startTime:Timestamp,
                         val endTime:Timestamp,
                         val allDay:Boolean=false,
                         val recurringEvent:String?=null,
                         val minutesBefore:Long=defaultMinutes,
                         val uid:String?=null) {
    companion object {
        private const val defaultMinutes = 15.toLong()
    }
    constructor(): this("", Timestamp.now(), Timestamp.now(), false, null, defaultMinutes, null)
}

data class Group(val groupName:String,
                 val uid:String?=null,
                 val members:List<String>) {
    constructor(): this("", null, emptyList())
}

data class Message(val sendingId:String,
                   val receivingId:String,
                   val msg:String,
                   val sentTime:Timestamp,
                   val read:Boolean=false) {
    constructor() : this("", "", "", Timestamp.now(), false)
}
