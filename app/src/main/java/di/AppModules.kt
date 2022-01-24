package di

import androidx.recyclerview.widget.RecyclerView
import com.example.binder.ui.usecase.ApproveFriendRequestsUseCase
import com.example.binder.ui.usecase.GetFriendRequestsUseCase
import com.example.binder.ui.usecase.GetFriendsUseCase
import com.example.binder.ui.usecase.GetGroupsUseCase
import com.example.binder.ui.usecase.GetVideoRoomUseCase
import com.example.binder.ui.usecase.GetVideoTokenUseCase
import com.example.binder.ui.viewholder.ViewHolderFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import okhttp3.OkHttpClient
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.factory
import org.koin.dsl.module
import repository.FirebaseRepository
import repository.RealtimeDB
import repository.RoomIdRepository
import repository.TokenRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import viewmodel.AddFriendFragmentViewModel
import viewmodel.HubFragmentViewModel
import viewmodel.InfoFragmentViewModel
import viewmodel.LoginFragmentViewModel
import viewmodel.MainActivityViewModel
import viewmodel.EditUserFragmentViewModel
import viewmodel.CalendarFragmentViewModel
import viewmodel.ChatFragmentViewModel
import viewmodel.DayScheduleFragmentViewModel
import viewmodel.FriendListFragmentViewModel
import viewmodel.FriendRequestFragmentViewModel
import viewmodel.InputScheduleBottomSheetViewModel
import viewmodel.ScheduleDisplayBottomSheetViewModel
import viewmodel.VideoMenuFragmentViewModel
import java.util.concurrent.TimeUnit

val appModule = module {

    single {
        FirebaseFirestore.getInstance()
    }

    single {
        FirebaseAuth.getInstance()
    }

    single {
        Firebase.database
    }

    single {
        Retrofit.Builder()
            .baseUrl("https://binder-conference-server.herokuapp.com/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .build()
            )
            .build()
    }

    factory {
        ViewHolderFactory()
    }

    factory {
        FirebaseRepository(get(), get())
    }

    factory {
        RoomIdRepository(get())
    }

    factory {
        TokenRepository(get())
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
        ApproveFriendRequestsUseCase<List<String>>(get())
    }

    factory {
        GetVideoTokenUseCase<Pair<String, String>>(get())
    }

    factory {
        GetVideoRoomUseCase<Pair<String,String>>(get())
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
    viewModel{
        ScheduleDisplayBottomSheetViewModel(get())
    }
    viewModel{
        ChatFragmentViewModel()
    }
    viewModel{
        AddFriendFragmentViewModel(get())
    }
    viewModel {
        FriendListFragmentViewModel(get(), get())
    }
    viewModel {
        FriendRequestFragmentViewModel(get(), get())
    }
    viewModel {
        VideoMenuFragmentViewModel(get(), get())
    }
}
