package di

import com.example.binder.ui.usecase.ApproveFriendRequestsUseCase
import com.example.binder.ui.usecase.CreateGroupUseCase
import com.example.binder.ui.usecase.DeleteGroupUseCase
import com.example.binder.ui.usecase.GetDMGroupAndUserUseCase
import com.example.binder.ui.usecase.GetFriendRequestsUseCase
import com.example.binder.ui.usecase.GetFriendStartingWithUseCase
import com.example.binder.ui.usecase.GetFriendsUseCase
import com.example.binder.ui.usecase.GetGroupsUseCase
import com.example.binder.ui.usecase.GetMoreMessagesUseCase
import com.example.binder.ui.usecase.RemoveFriendUseCase
import com.example.binder.ui.usecase.SendMessageUseCase
import com.example.binder.ui.usecase.UpdateMessagingTokenUseCase
import com.example.binder.ui.viewholder.ViewHolderFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import data.Message
import data.User
import data.Group
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import repository.FirebaseRepository
import repository.RealtimeDB
import viewmodel.AddFriendFragmentViewModel
import viewmodel.HubFragmentViewModel
import viewmodel.InfoFragmentViewModel
import viewmodel.LoginFragmentViewModel
import viewmodel.MainActivityViewModel
import viewmodel.EditUserFragmentViewModel
import viewmodel.CalendarFragmentViewModel
import viewmodel.ChatFragmentViewModel
import viewmodel.CreateGroupFragmentViewModel
import viewmodel.DayScheduleFragmentViewModel
import viewmodel.FriendListFragmentViewModel
import viewmodel.FriendRequestFragmentViewModel
import viewmodel.InputScheduleBottomSheetViewModel
import viewmodel.ScheduleDisplayBottomSheetViewModel

val appModule = module {

    single {
        FirebaseFirestore.getInstance()
    }

    single {
        FirebaseAuth.getInstance()
    }

    single {
        FirebaseMessaging.getInstance()
    }

    single {
        Firebase.database
    }

    factory {
        ViewHolderFactory()
    }

    factory {
        FirebaseRepository(get(), get())
    }

    factory {
        RealtimeDB(get())
    }

    factory {
        GetFriendsUseCase(get())
    }

    factory {
        GetGroupsUseCase(get())
    }

    factory {
        GetFriendRequestsUseCase(get())
    }

    factory {
        ApproveFriendRequestsUseCase(get())
    }

    factory {
        SendMessageUseCase(get())
    }

    factory {
        GetMoreMessagesUseCase(get())
    }

    factory {
        UpdateMessagingTokenUseCase(get(), get())
    }

    factory {
        RemoveFriendUseCase(get())
    }

    factory {
        GetFriendStartingWithUseCase<String>(get())
    }

    factory {
        CreateGroupUseCase<Group>(get())
    }

    factory {
        DeleteGroupUseCase(get())
    }

    viewModel {
        MainActivityViewModel(get())
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
    viewModel {
        ChatFragmentViewModel(get(), get(), get())
    }
    viewModel {
        ScheduleDisplayBottomSheetViewModel(get())
    }
    viewModel{
        AddFriendFragmentViewModel(get(), get())
    }
    viewModel {
        FriendListFragmentViewModel(get(), get())
    }
    viewModel {
        FriendRequestFragmentViewModel(get(), get())
    }
    viewModel {
        CreateGroupFragmentViewModel(get(), get())
    }
}
