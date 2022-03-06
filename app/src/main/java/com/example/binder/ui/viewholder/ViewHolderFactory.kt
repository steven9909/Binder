package com.example.binder.ui.viewholder

import android.view.ViewGroup
import com.example.binder.ui.Item
import com.example.binder.ui.OnActionListener
import com.example.binder.ui.fragment.VideoPlayerFragment

class ViewHolderFactory {

    companion object {
        const val EMPTY_TYPE = 0
        const val MESSAGE_TITLE_TYPE = 1
        const val MESSAGE_BODY_TYPE = 2
        const val INTEREST_TYPE = 3
        const val FRIEND_DETAIL_TYPE = 4
        const val FRIEND_NAME_TYPE = 5
        const val LIST_HEADER_TYPE = 6
        const val DIVIDER_TYPE = 7
        const val VIDEO_PLAYER_TYPE = 8
        const val FILE_DETAIL_TYPE = 9
        const val QUESTION_DETAIL_TYPE = 10
        const val GROUP_CATEGORY_TYPE = 11
        const val MESSAGE_SENT_BY_TYPE = 12
        const val VIDEO_USER_TYPE = 13
        const val VIDEO_SCHEDULED_CALLS_TYPE = 14
    }

    @SuppressWarnings("ComplexMethod")
    fun getViewHolder(
        parent: ViewGroup,
        type: Int,
        actionListener: OnActionListener,
        getItem: (Int) -> Item
    ): BaseViewHolder<Item> {
        return when (type) {
            EMPTY_TYPE -> EmptyViewHolder(parent, actionListener)
            MESSAGE_TITLE_TYPE -> MessageTitleViewHolder(parent, actionListener)
            MESSAGE_BODY_TYPE -> MessageViewHolder(parent, actionListener)
            INTEREST_TYPE -> InterestViewHolder(parent, actionListener)
            FRIEND_DETAIL_TYPE -> FriendDetailViewHolder(parent, actionListener, getItem)
            FRIEND_NAME_TYPE -> FriendNameViewHolder(parent, actionListener)
            LIST_HEADER_TYPE -> ListHeaderViewHolder(parent, actionListener)
            VIDEO_PLAYER_TYPE -> VideoPlayerViewHolder(parent, actionListener, getItem)
            FILE_DETAIL_TYPE -> FileDetailViewHolder(parent, actionListener)
            QUESTION_DETAIL_TYPE -> QuestionDetailViewHolder(parent, actionListener)
            GROUP_CATEGORY_TYPE -> GroupTypeViewHolder(parent, actionListener)
            MESSAGE_SENT_BY_TYPE -> MessageSentByViewHolder(parent, actionListener)
            VIDEO_USER_TYPE -> VideoUserViewHolder(parent, actionListener, getItem)
            VIDEO_SCHEDULED_CALLS_TYPE -> VideoScheduledCallsViewHolder(parent, actionListener, getItem)
            else -> EmptyViewHolder(parent, actionListener)
        }
    }
}
