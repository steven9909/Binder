package data

data class User(val userId:String, val school:String, val program:String, val interests:String, val name:String?=null) {

}

data class Settings(val enableNotifications:Boolean=true, val language:String="English", val customStatus:String="Active") {

}

data class Friends(val friendIds:Set<String>, val friendNames:List<String>) {

}