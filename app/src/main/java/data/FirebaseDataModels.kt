package data

import com.google.firebase.Timestamp

data class User(val userId:String,
                val school:String,
                val program:String,
                val interests:String,
                val name:String?=null,
                val guid:Set<String>?=null) {
    constructor(): this("", "", "", "", null, null)
}

data class Settings(val enableNotifications:Boolean=true,
                    val language:String="English",
                    val customStatus:String="Active") {
    constructor(): this(true, "", "")
}

data class Friends(val friendIds:Set<String>,
                   val friendNames:List<String>) {
    constructor(): this(emptySet(), emptyList())
}

data class CalendarEvent(val name:String,
                         val startTime:Timestamp,
                         val endTime:Timestamp,
                         val allDay:Boolean=false,
                         val recurringEvent:String?=null,
                         val minutesBefore:Long=defaultMinutes,
                         val cid:String?=null) {
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
                   var sentTime:Timestamp) {
    constructor() : this("", "", "", Timestamp.now())
}