package data

import java.sql.Timestamp
import java.util.*

data class User(val userId:String, val school:String, val program:String, val interests:String, val name:String?=null) {

}

data class Settings(val enableNotifications:Boolean=true, val language:String="English", val customStatus:String="Active") {

}

data class Friends(val friendIds:Set<String>, val friendNames:List<String>) {

}

data class CalendarEvent(val name:String, val startTime:Timestamp, val endTime: Timestamp, val timeZone: TimeZone,
                         val allDay:Boolean=false, val recurringEvent:String="", val minutesBefore:Int=15) {

}