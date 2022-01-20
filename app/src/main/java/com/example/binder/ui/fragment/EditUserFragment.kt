package com.example.binder.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.example.binder.databinding.LayoutEditUserFragmentBinding
import data.EditUserConfig
import data.User
import org.koin.androidx.viewmodel.ext.android.viewModel
import viewmodel.EditUserFragmentViewModel
import viewmodel.InfoFragmentViewModel

class EditUserFragment(override val config: EditUserConfig) : BaseFragment() {

    override val viewModel: ViewModel by viewModel<EditUserFragmentViewModel>()

    private var binding: LayoutEditUserFragmentBinding? = null

    private lateinit var userInfo: User

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutEditUserFragmentBinding.inflate(inflater, container, false)
        setupUi()
        return binding!!.root
    }

    private fun setupUi(){
        binding?.let { binding ->
            (viewModel as EditUserFragmentViewModel).getUserInformation().observe(viewLifecycleOwner){ user->
                if (user.status == Status.SUCCESS && user.data != null) {
                    userInfo = user.data
                    binding.whatNameEdit.setText(userInfo.name)
                    binding.whatProgramEdit.setText(userInfo.program)
                    binding.whatSchoolEdit.setText(userInfo.school)
                    binding.whatInterestEdit.setText(userInfo.interests)
                }
            }
            binding.confirmChangeButton.setOnClickListener{
                if(binding.whatNameEdit.isDirty){}
                if(binding.whatProgramEdit.isDirty){}
                if(binding.whatSchoolEdit.isDirty){}
                if(binding.whatInterestEdit.isDirty){}
                (viewModel as EditUserFragmentViewModel).setUpdateUserInformation(userInfo)
            }
        }
    }
}
