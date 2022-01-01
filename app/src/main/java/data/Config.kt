package data

abstract class Config {
    open val shouldBeAddedToBackstack: Boolean = true
}

class HubConfig (val name: String, override val shouldBeAddedToBackstack: Boolean = false): Config() {

}

class LoginConfig (override val shouldBeAddedToBackstack: Boolean = false): Config() {

}

class InfoConfig(val name: String, val uid: String, override val shouldBeAddedToBackstack: Boolean = false): Config() {

}

class EditUserConfig(): Config() {

}

class FriendFinderConfig: Config() {

}

class CalendarConfig(): Config() {

}