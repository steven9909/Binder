package com.example.binder.ui.fragment

import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import android.os.Bundle
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import com.example.binder.R
import com.example.binder.databinding.LayoutSideDrawerBinding
import com.example.binder.ui.ClickInfo
import com.example.binder.ui.GenericListAdapter
import com.example.binder.ui.OnActionListener
import com.example.binder.ui.recyclerview.VerticalSpaceItemDecoration
import com.example.binder.ui.viewholder.ViewHolderFactory
import data.GroupMemberDrawerConfig
import org.koin.android.ext.android.inject


abstract class GroupMemberDrawerFragment(override val config: GroupMemberDrawerConfig) : BaseFragment() {

    companion object{
        private const val VERTICAL_SPACING = 10
    }

    private var binding: LayoutSideDrawerBinding? = null

    private lateinit var genericListAdapter: GenericListAdapter

    private val viewHolderFactory: ViewHolderFactory by inject()

    private var mDrawerToggle: ActionBarDrawerToggle? = null

    private var mDrawerLayout: DrawerLayout? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutSideDrawerBinding.inflate(inflater, container, false)
        setupUI()
        return view
    }

    private val actionListener = object: OnActionListener {
        override fun onViewSelected(index: Int, clickInfo: ClickInfo?) {

        }
    }

    private fun setupUI() {
        binding?.let { binding ->
            genericListAdapter = GenericListAdapter(viewHolderFactory, actionListener)
            binding.drawerList.layoutManager = LinearLayoutManager(context)
            binding.drawerList.adapter = genericListAdapter
            binding.drawerList.addItemDecoration(
                VerticalSpaceItemDecoration(VERTICAL_SPACING)
            )
        }
    }

    fun setUpDrawer(fragmentId: Int, drawerLayout: DrawerLayout?) {
        mDrawerLayout = drawerLayout
        mDrawerToggle = object : ActionBarDrawerToggle(
            this.requireActivity(),
            drawerLayout,
            R.string.drawer_open,
            R.string.drawer_close
        ) {
            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                //                getActivity().invalidateOptionsMenu();
            }

            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
                //                getActivity().invalidateOptionsMenu();
            }

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                super.onDrawerSlide(drawerView, slideOffset)
                // Do something of Slide of Drawer
            }
        }
        // this drawer layout is linked with ActionBarDrawerToggle
        mDrawerLayout!!.addDrawerListener(mDrawerToggle as ActionBarDrawerToggle)

        // sync the state of Navigation Drawer with the help of Runnable
        mDrawerLayout!!.post { (mDrawerToggle as ActionBarDrawerToggle).syncState() }
    }

}