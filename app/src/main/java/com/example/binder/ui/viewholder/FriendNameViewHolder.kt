package com.example.binder.ui.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.binder.databinding.LayoutFriendNameViewHolderBinding
import com.example.binder.ui.ClickInfo
import com.example.binder.ui.ClickType
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

    override fun bindView(item: Item) {
        (item as? FriendNameItem)?.let { item ->
            (binding as? LayoutFriendNameViewHolderBinding)?.let { binding ->
                binding.nameText.text = item.name
                binding.nameText.setOnClickListener {
                    listener.onViewSelected(bindingAdapterPosition, object: ClickInfo{
                        override fun getType() = null
                        override fun getSource() = item.guid
                        override fun getName() = item.name
//                        override fun getOwner() = item.owner
                    })
                    listener.onViewSelected(item)
                }
            }
        }
    }

    override fun recycle() {
        Unit
    }
}

data class FriendNameItem(
    override val uid: String?,
    val name: String?,
    val guid: String?,
    val owner: String?,
    val members: List<String>?,
    val friendNameType: String,
    override val type: Int = FRIEND_NAME_TYPE
): Item()
