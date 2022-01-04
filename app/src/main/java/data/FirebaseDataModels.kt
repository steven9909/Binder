package data

import java.time.ZoneId
import com.google.firebase.Timestamp

data class User(val userId:String,
                val school:String,
                val program:String,
                val interests:String,
                val name:String?=null) {
    constructor(): this("", "", "", "", "")
}

data class Settings(val enableNotifications:Boolean=true,
                    val language:String="English",
                    val customStatus:String="Active") {
    constructor(): this(true, "", "")
}

data class Friends(val friendIds:Set<String>,
                   val friendNames:List<String>) {
    constructor(): this(setOf<String>(), listOf<String>())
}

data class CalendarEvent(val name:String,
                         val startTime: Timestamp,
                         val endTime: Timestamp,
                         val allDay:Boolean=false,
                         val recurringEvent:String="",
                         val minutesBefore:Long=defaultMinutes) {
    companion object {
        private const val defaultMinutes = 15.toLong()
    }
    constructor(): this("", Timestamp.now(), Timestamp.now(), false, "", defaultMinutes)
}
