package viewmodel


class FriendRecommendationFragmentViewModel : BaseViewModel() {

    private val marked = mutableSetOf<Int>()

    fun addMarkedIndex(index: Int) {
        marked.add(index)
    }
    fun removeMarkedIndex(index: Int){
        marked.remove(index)
    }
}
