package com.example.binder.ui.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.binder.databinding.LayoutGroupTypeViewHolderBinding
import com.example.binder.ui.Item
import com.example.binder.ui.OnActionListener
import com.example.binder.ui.viewholder.ViewHolderFactory.Companion.GROUP_CATEGORY_TYPE

class GroupTypeViewHolder(parent: ViewGroup, listener: OnActionListener) : BaseViewHolder<Item>(
    listener,
    LayoutGroupTypeViewHolderBinding.inflate(
        LayoutInflater.from(parent.context),
        parent, false
    )
) {
    override val type: Int
        get() = GROUP_CATEGORY_TYPE

    override fun bindView(item: Item) {
        (item as? GroupTypeItem)?.let {
            (binding as? LayoutGroupTypeViewHolderBinding)?.let {
                binding.typeText.text = item.groupType
                binding.deleteButton.setOnClickListener {
                    listener.onDeleteRequested(bindingAdapterPosition)
                }
            }
        }
    }

    override fun recycle() {
        Unit
    }
}

data class GroupTypeItem(
    override val uid: String? = null,
    val groupType:String,
    override val type: Int = GROUP_CATEGORY_TYPE
): Item()
