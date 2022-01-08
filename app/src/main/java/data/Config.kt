package data

sealed class Config {
    open val shouldBeAddedToBackstack: Boolean = true
}

sealed class BottomSheetConfig(override val shouldBeAddedToBackstack: Boolean = false): Config()

class HubConfig (val name: String, val uid: String,
                 override val shouldBeAddedToBackstack: Boolean = false): Config()

class LoginConfig (override val shouldBeAddedToBackstack: Boolean = false): Config()

class InfoConfig(val name: String, val uid: String, override val shouldBeAddedToBackstack: Boolean = false): Config()

class EditUserConfig: Config()

class FriendFinderConfig: Config()

class CalendarConfig: Config()

class DayScheduleConfig: Config()

class InputScheduleBottomSheetConfig: BottomSheetConfig()

class ChatConfig: Config()

class AddFriendConfig(val name: String, val uid: String): Config()
