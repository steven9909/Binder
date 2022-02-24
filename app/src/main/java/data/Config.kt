package data

import com.example.binder.ui.Item

sealed class Config {
    open val shouldBeAddedToBackstack: Boolean = true
    open val shouldOpenInStaticSheet: Boolean = false
}

sealed class BottomSheetConfig(override val shouldBeAddedToBackstack: Boolean = false): Config()

class HubConfig (val name: String, val uid: String,
                 override val shouldBeAddedToBackstack: Boolean = false): Config()

class LoginConfig (override val shouldBeAddedToBackstack: Boolean = false): Config()

class InfoConfig(val name: String, val uid: String, override val shouldBeAddedToBackstack: Boolean = false): Config()

class VideoUserBottomSheetConfig(val people: MutableList<Item>) : BottomSheetConfig()

class VideoPlayerConfig(val name: String, val uid: String, val token: String): Config()

class VideoConfig(val name: String, val uid: String, override val shouldOpenInStaticSheet: Boolean = false): Config()

class EditUserConfig(var name: String, val uid: String): Config()

class FriendFinderConfig: Config()

class SettingsConfig(
    override val shouldOpenInStaticSheet: Boolean = false
): Config()

class CalendarSelectConfig(val name: String, val uid: String): Config()

class CalendarConfig(val name: String, val uid: String, val isGroupOwner: Boolean = false,
                     override val shouldOpenInStaticSheet: Boolean = false): Config()

class DayScheduleConfig(val name: String, val uid: String, val month: Int, val day: Int,
                        val year: Int, val isGroupOwner: Boolean = false): Config()

class InputScheduleBottomSheetConfig(val calendarEvent: CalendarEvent? = null): BottomSheetConfig()

class ScheduleDisplayBottomSheetConfig(val name: String, val uid: String,
                                       val calendarEvent: CalendarEvent,
                                       val isGroupOwner: Boolean = false): BottomSheetConfig()

class ChatConfig(val name: String, val uid: String, val guid: String, val chatName: String): Config()

class AddFriendConfig(val name: String, val uid: String): Config()

class FriendListConfig(
    val name: String,
    val uid: String,
    override val shouldOpenInStaticSheet: Boolean = false
): Config()

class FriendRequestConfig(val name: String, val uid: String): Config()

class CreateGroupConfig(val name: String, val uid: String): Config()

class FriendRecommendationConfig(val name: String, val uid: String): Config()

class InputQuestionBottomSheetConfig(val name: String,
                                     val uid: String,
                                     val guid: String,
                                     val chatName: String): BottomSheetConfig()
