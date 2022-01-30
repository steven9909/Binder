package data

sealed class Config {
    open val shouldBeAddedToBackstack: Boolean = true
}

sealed class BottomSheetConfig(override val shouldBeAddedToBackstack: Boolean = false): Config()

class HubConfig (val name: String, val uid: String,
                 override val shouldBeAddedToBackstack: Boolean = false): Config()

class LoginConfig (override val shouldBeAddedToBackstack: Boolean = false): Config()

class InfoConfig(val name: String, val uid: String, override val shouldBeAddedToBackstack: Boolean = false): Config()

class VideoPlayerConfig(val name: String, val uid: String): Config()

//class HMSConfig : Config()

class VideoConfig(val name: String, val uid: String): Config()

class EditUserConfig(var name: String, val uid: String): Config()

class FriendFinderConfig: Config()

class SettingsConfig: Config()

class CalendarConfig(val name: String, val uid: String): Config()

class DayScheduleConfig(val name: String, val uid: String, val month: Int, val day: Int, val year: Int): Config()

class InputScheduleBottomSheetConfig: BottomSheetConfig()

class ScheduleDisplayBottomSheetConfig(val name: String, val uid: String, val calendarEvent: CalendarEvent): BottomSheetConfig()

class ChatConfig(val name: String, val uid: String, val guid: String, val chatName: String): Config()

class AddFriendConfig(val name: String, val uid: String): Config()

class FriendListConfig(val name: String, val uid: String): Config()

class FriendRequestConfig(val name: String, val uid: String): Config()

class CreateGroupConfig(val name: String, val uid: String): Config()

class FriendRecommendationConfig(val name: String, val uid: String): Config()

class InputQuestionBottomSheetConfig(val name: String,
                                     val uid: String,
                                     val guid: String,
                                     val chatName: String): BottomSheetConfig()
