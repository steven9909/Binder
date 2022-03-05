package com.example.binder.ui.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.binder.databinding.LayoutGroupTypeViewHolderBinding
import com.example.binder.ui.Item
import com.example.binder.ui.OnActionListener
import com.example.binder.ui.viewholder.ViewHolderFactory.Companion.GROUP_CATEGORY_TYPE
import setVisibility

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
                if (item.showDeleteButton){
                    binding.deleteButton.setOnClickListener {
                        listener.onDeleteRequested(bindingAdapterPosition)
                    }
                } else {
                    binding.deleteButton.visibility = View.GONE
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
    val showDeleteButton:Boolean,
    override val type: Int = GROUP_CATEGORY_TYPE
): Item()
