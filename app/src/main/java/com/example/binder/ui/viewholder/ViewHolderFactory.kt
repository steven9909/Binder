package com.example.binder.ui.viewholder

import android.view.ViewGroup
import com.example.binder.ui.Item
import com.example.binder.ui.OnActionListener

class ViewHolderFactory {

    companion object {
        const val EMPTY_TYPE = 0
        const val MESSAGE_TITLE_TYPE = 1
        const val MESSAGE_BODY_TYPE = 2
    }

    fun getViewHolder(parent: ViewGroup, type: Int, actionListener: OnActionListener): BaseViewHolder<Item> {
        return when (type) {
            EMPTY_TYPE -> EmptyViewHolder(parent, actionListener)
            MESSAGE_TITLE_TYPE -> MessageTitleViewHolder(parent, actionListener)
            MESSAGE_BODY_TYPE -> MessageViewHolder(parent, actionListener)
            else -> EmptyViewHolder(parent, actionListener)
        }
    }
}