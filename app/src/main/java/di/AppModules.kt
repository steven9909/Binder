package di

import com.google.firebase.firestore.FirebaseFirestore
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import repository.FirebaseRepository
import viewmodel.*

val appModule = module {

    single {
        FirebaseFirestore.getInstance()
    }

    factory {
        FirebaseRepository(get())
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
        EditUserFragmentViewModel()
    }
}