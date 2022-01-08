package com.example.binder.ui.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import changeBackgroundColor
import com.example.binder.R
import com.example.binder.databinding.LayoutFriendDetailViewHolderBinding
import com.example.binder.ui.Item
import com.example.binder.ui.OnActionListener

class FriendDetailViewHolder(parent: ViewGroup, listener: OnActionListener) : BaseViewHolder<Item>(
    listener,
    LayoutFriendDetailViewHolderBinding.inflate(
        LayoutInflater.from(parent.context),
        parent, false
    )
) {
    override val type: Int
        get() = ViewHolderFactory.FRIEND_DETAIL_TYPE

    private var isClicked: Boolean = false

    override fun bindView(item: Item, position: Int) {
        (item as? FriendDetailItem)?.let { item ->
            (binding as? LayoutFriendDetailViewHolderBinding)?.let { binding ->
                binding.nameText.text = context.getString(R.string.name).format(item.name)
                binding.interestText.text = context.getString(R.string.interests).format(item.interest)
                binding.schoolText.text = context.getString(R.string.school).format(item.school)
                binding.programText.text = context.getString(R.string.program).format(item.program)

                binding.root.changeBackgroundColor(context.getColor(R.color.white))

                binding.root.setOnClickListener {
                    isClicked = !isClicked
                    when (isClicked) {
                        true -> {
                            binding.root.changeBackgroundColor(context.getColor(R.color.app_grey))
                            listener.onViewSelected(position)
                        }
                        false -> {
                            binding.root.changeBackgroundColor(context.getColor(R.color.white))
                            listener.onViewUnSelected(position)
                        }
                    }
                }
            }
        }
    }

    override fun recycle() {
        isClicked = false
        binding.root.changeBackgroundColor(context.getColor(R.color.white))
    }
}

data class FriendDetailItem(
    val uid: String,
    val name:String,
    val school:String,
    val program:String,
    val interest:String,
    override val type: Int = ViewHolderFactory.FRIEND_DETAIL_TYPE
): Item()
