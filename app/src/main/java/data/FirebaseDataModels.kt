package data

import java.time.ZoneId

data class User(val userId:String, val school:String, val program:String, val interests:String, val name:String?=null) {
    constructor(): this("", "", "", "", "")
}

data class Settings(val enableNotifications:Boolean=true, val language:String="English", val customStatus:String="Active") {
    constructor(): this(true, "", "")
}

data class Friends(val friendIds:Set<String>, val friendNames:List<String>) {
    constructor(): this(setOf<String>(), listOf<String>())
}

data class CalendarEvent(val name:String, val startTime: java.time.Instant, val endTime: java.time.Instant, val timeZone: ZoneId,
                         val allDay:Boolean=false, val recurringEvent:String="", val minutesBefore:Int=15) {
    constructor(): this("", java.time.Instant.now(), java.time.Instant.now(), ZoneId.systemDefault(), false, "", 15)
}