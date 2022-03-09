package com.example.binder.ui.viewholder

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.binder.R
import com.example.binder.databinding.LayoutVideoScheduledCallsViewHolderBinding
import com.example.binder.databinding.LayoutVideoUsersViewHolderBinding
import com.example.binder.ui.Item
import com.example.binder.ui.OnActionListener
import timber.log.Timber
import java.util.*

class VideoScheduledCallsViewHolder(
    parent: ViewGroup,
    listener: OnActionListener,
    private val getItem: (Int) -> Item) : BaseViewHolder<Item> (
    listener,
    LayoutVideoScheduledCallsViewHolderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
)   {
    override val type: Int
        get() = ViewHolderFactory.VIDEO_SCHEDULED_CALLS_TYPE
    private var isInit: Boolean = false


    override fun bindView(item: Item) {

        (item as? ScheduledCallItem)?.let { item ->
            (binding as? LayoutVideoScheduledCallsViewHolderBinding)?.let { binding ->
                Timber.d("VideoMenuFragment: ${item.title}, ${item.startTime}, ${item.endTime }")
                binding.scheduleNameText.text = item.title
//                binding.scheduleNameText.setTextColor(2)
                binding.dateText.text = item.startTime.toString()
                binding.timeText.text = item.endTime.toString()
            }
        }

    }

    override fun recycle() {
        //test
    }

}

data class ScheduledCallItem(
    val id: Long,
    override val uid: String,
    val title: String,
    val startTime: Date,
    val endTime: Date,
    override val type: Int = ViewHolderFactory.VIDEO_SCHEDULED_CALLS_TYPE
): Item()