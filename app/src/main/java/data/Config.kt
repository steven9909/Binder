package data

abstract class Config {
    open val shouldBeAddedToBackstack: Boolean = true
}

class LoginConfig (override val shouldBeAddedToBackstack: Boolean = false): Config() {

}

class InfoConfig(val name: String, override val shouldBeAddedToBackstack: Boolean = false): Config() {

}