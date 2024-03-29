package data

import com.google.firebase.firestore.Exclude

sealed class BaseData {
    open val uid: String? = null
}

data class User(val school:String?,
                val program:String?,
                val interests:List<String>?,
                val name:String?=null,
                val token:String?=null,
                val userGroups:List<String>,
                @get:Exclude override val uid: String?=null): BaseData() {
    constructor(): this("", "", null, null, null, emptyList(), null)
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
                         val receiverId:String?,
                         @get:Exclude override val uid: String?=null): BaseData() {
    constructor(): this("", "", null)
}

data class CalendarEvent(val name:String,
                         val startTime:Long,
                         val endTime:Long,
                         val allDay:Boolean=false,
                         val recurringEvent:String?=null,
                         val recurringEnd:Long?=null,
                         val minutesBefore:Long=defaultMinutes,
                         @get:Exclude override val uid: String?=null): BaseData() {
    companion object {
        private const val defaultMinutes = 15.toLong()
    }
    constructor(): this("", 0L, 0L, false, null, null, defaultMinutes, null)
}

data class Group(val groupName:String,
                 val members:List<String>,
                 val owner:String,
                 val dm:Boolean,
                 val groupTypes:List<String>,
                 @get:Exclude override val uid: String?=null): BaseData() {
    constructor(): this("", emptyList(), "", false, emptyList(), null)
}

data class Question(val question:String,
                    val answers:List<String>,
                    val answerIndexes:List<Int>,
                    val questionType:String?,
                    @get:Exclude override val uid: String?=null): BaseData() {
    constructor(): this("", emptyList(), emptyList(), null, null)
}

data class Message(val sendingId:String,
                   val msg:String,
                   val timestamp:Long,
                   val fileLink: String?,
                   val question: Question?,
                   val sentByName:String?,
                   @get:Exclude override val uid: String?=null): BaseData() {
    constructor() : this("", "", 0L, null, null,null)
}
