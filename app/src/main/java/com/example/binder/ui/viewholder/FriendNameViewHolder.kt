package com.example.binder.ui.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.binder.databinding.LayoutFriendDetailViewHolderBinding
import com.example.binder.databinding.LayoutFriendNameViewHolderBinding
import com.example.binder.ui.Item
import com.example.binder.ui.OnActionListener
import com.example.binder.ui.viewholder.ViewHolderFactory.Companion.FRIEND_NAME_TYPE

class FriendNameViewHolder(parent: ViewGroup, listener: OnActionListener) : BaseViewHolder<Item>(
    listener,
    LayoutFriendNameViewHolderBinding.inflate(
        LayoutInflater.from(parent.context),
        parent, false
    )
) {
    override val type: Int = FRIEND_NAME_TYPE

    override fun bindView(item: Item, position: Int) {
        (item as? FriendNameItem)?.let { item ->
            (binding as? LayoutFriendNameViewHolderBinding)?.let { binding ->
                binding.nameText.text = item.name
            }
        }
    }

    override fun recycle() {
        Unit
    }
}

data class FriendNameItem(val uid: String?, val name: String?, override val type: Int = FRIEND_NAME_TYPE): Item()
