package com.example.binder.ui.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.binder.databinding.LayoutInterestViewHolderBinding
import com.example.binder.ui.Item
import com.example.binder.ui.OnActionListener
import com.example.binder.ui.viewholder.ViewHolderFactory.Companion.INTEREST_TYPE

class InterestViewHolder(parent: ViewGroup, listener: OnActionListener) : BaseViewHolder<Item>(
    listener,
    LayoutInterestViewHolderBinding.inflate(
        LayoutInflater.from(parent.context),
        parent, false
    )
) {

    override val type: Int
        get() = INTEREST_TYPE

    override fun bindView(item: Item) {
        (item as? InterestItem)?.let {
            (binding as? LayoutInterestViewHolderBinding)?.let {
                binding.interestText.text = item.interest
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

data class InterestItem(
    override val uid: String? = null,
    val interest:String,
    override val type: Int = INTEREST_TYPE
): Item()
