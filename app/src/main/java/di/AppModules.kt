package di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import repository.FirebaseRepository
import viewmodel.HubFragmentViewModel
import viewmodel.InfoFragmentViewModel
import viewmodel.LoginFragmentViewModel
import viewmodel.MainActivityViewModel
import viewmodel.EditUserFragmentViewModel
import viewmodel.CalendarFragmentViewModel
import viewmodel.DayScheduleFragmentViewModel
import viewmodel.InputScheduleBottomSheetViewModel

val appModule = module {

    single {
        FirebaseFirestore.getInstance()
    }

    single {
        FirebaseAuth.getInstance()
    }

    factory {
        FirebaseRepository(get(), get())
    }

    viewModel {
        MainActivityViewModel()
    }
    viewModel {
        InfoFragmentViewModel(get())
    }
    viewModel {
        HubFragmentViewModel()
    }
    viewModel {
        LoginFragmentViewModel()
    }
    viewModel {
        EditUserFragmentViewModel(get())
    }
    viewModel{
        CalendarFragmentViewModel(get())
    }
    viewModel{
        DayScheduleFragmentViewModel(get())
    }
    viewModel{
        InputScheduleBottomSheetViewModel()
    }
}
