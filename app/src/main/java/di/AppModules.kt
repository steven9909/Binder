package di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import viewmodel.MainActivityViewModel

val appModule = module {
    viewModel {
        MainActivityViewModel()
    }
}