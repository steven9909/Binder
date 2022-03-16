package di

import com.example.binder.ui.GoogleAccountProvider
import com.example.binder.ui.usecase.AddQuestionToDBUseCase
import com.example.binder.ui.usecase.ApproveFriendRequestsUseCase
import com.example.binder.ui.usecase.BatchCalendarEventUpdateUseCase
import com.example.binder.ui.usecase.CreateGroupUseCase
import com.example.binder.ui.usecase.DeleteGroupUseCase
import com.example.binder.ui.usecase.DeleteScheduleUseCase
import com.example.binder.ui.usecase.DoesUserExistUseCase
import com.example.binder.ui.usecase.FriendRecommendationUseCase
import com.example.binder.ui.usecase.GetFriendRequestsUseCase
import com.example.binder.ui.usecase.GetFriendStartingWithUseCase
import com.example.binder.ui.usecase.GetFriendsUseCase
import com.example.binder.ui.usecase.GetGroupTypesUseCase
import com.example.binder.ui.usecase.GetGroupsUseCase
import com.example.binder.ui.usecase.GetScheduleUseCase
import com.example.binder.ui.usecase.GetVideoRoomUseCase
import com.example.binder.ui.usecase.GetVideoTokenUseCase
import com.example.binder.ui.usecase.GetMoreMessagesUseCase
import com.example.binder.ui.usecase.GetQuestionFromDBUseCase
import com.example.binder.ui.usecase.GetRecordingUseCase
import com.example.binder.ui.usecase.GetScheduleForUserUseCase
import com.example.binder.ui.usecase.GetSpecificUserUseCase
import com.example.binder.ui.usecase.GetUserInformationUseCase
import com.example.binder.ui.usecase.RemoveFriendUseCase
import com.example.binder.ui.usecase.RemoveGroupMemberUseCase
import com.example.binder.ui.usecase.SendFriendRequestsUseCase
import com.example.binder.ui.usecase.SendMessageUseCase
import com.example.binder.ui.usecase.UpdateGroupNameUseCase
import com.example.binder.ui.usecase.UpdateUserInformationUserCase
import com.example.binder.ui.usecase.UpdateMessagingTokenUseCase
import com.example.binder.ui.usecase.UpdateScheduleUseCase
import com.example.binder.ui.viewholder.ViewHolderFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import okhttp3.OkHttpClient
import com.google.firebase.messaging.FirebaseMessaging
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import repository.FirebaseRepository
import repository.FriendRecommendationRepository
import repository.RealtimeDB
import repository.RecordingRepository
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
import viewmodel.CalendarSelectViewModel
import viewmodel.ChatFragmentViewModel
import viewmodel.ChatMoreOptionsBottomSheetViewModel
import viewmodel.CreateGroupFragmentViewModel
import viewmodel.DayScheduleFragmentViewModel
import viewmodel.EditGroupFragmentViewModel
import viewmodel.FriendListFragmentViewModel
import viewmodel.FriendProfileFragmentViewModel
import viewmodel.FriendRecommendationFragmentViewModel
import viewmodel.FriendRequestFragmentViewModel
import viewmodel.InputQuestionBottomSheetViewModel
import viewmodel.InputScheduleBottomSheetViewModel
import viewmodel.ScheduleDisplayBottomSheetViewModel
import viewmodel.SharedVideoPlayerViewModel
import viewmodel.VideoMenuFragmentViewModel
import java.util.concurrent.TimeUnit

@SuppressWarnings("MagicNumber")
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
        GoogleAccountProvider(androidContext())
    }

    single {
        Firebase.database
    }

    single<Retrofit>(named("friendRecommend")){
        Retrofit.Builder()
            .baseUrl("https://binder-recommendations.herokuapp.com/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .build()
            )
            .build()
    }

    single<Retrofit>(named("tokenGen")){
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
        RecordingRepository(get(named("tokenGen")))
    }

    factory {
        RoomIdRepository(get(named("tokenGen")))
    }

    factory {
        TokenRepository(get(named("tokenGen")))
    }

    factory {
        FriendRecommendationRepository(get(named("friendRecommend")))
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
        UpdateUserInformationUserCase(get())
    }

    factory {
        GetUserInformationUseCase(get())
    }

    factory {
        GetFriendStartingWithUseCase(get())
    }

    factory {
        CreateGroupUseCase(get())
    }

    factory {
        DeleteGroupUseCase(get())
    }

    factory {
        RemoveGroupMemberUseCase(get())
    }

    factory {
        AddQuestionToDBUseCase(get())
    }

    factory {
        GetQuestionFromDBUseCase(get())
    }

    factory {
        GetGroupTypesUseCase(get())
    }

    factory {
        BatchCalendarEventUpdateUseCase(get())
    }

    factory {
        GetScheduleUseCase(get())
    }

    factory {
        UpdateScheduleUseCase(get())
    }

    factory {
        GetScheduleForUserUseCase(get())
    }

    factory {
        GetRecordingUseCase<String>(get())
    }

    factory {
        GetVideoTokenUseCase<Pair<String, String>>(get())
    }

    factory {
        GetVideoRoomUseCase<Pair<String,String>>(get())
    }

    factory {
        DeleteScheduleUseCase(get())
    }

    factory {
        DoesUserExistUseCase(get())
    }

    factory {
        GetSpecificUserUseCase(get())
    }

    factory {
        UpdateGroupNameUseCase(get())
    }

    factory {
        FriendRecommendationUseCase(get())
    }

    factory {
        SendFriendRequestsUseCase(get())
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
        LoginFragmentViewModel(get())
    }
    viewModel {
        EditUserFragmentViewModel(get(), get())
    }
    viewModel{
        CalendarFragmentViewModel(get(), get())
    }
    viewModel{
        CalendarSelectViewModel(get())
    }
    viewModel{
        DayScheduleFragmentViewModel(get(), get())
    }
    viewModel{
        InputScheduleBottomSheetViewModel(get(), get())
    }
    viewModel {
        ChatFragmentViewModel(get(), get(), get(), get(), get(), get())
    }
    viewModel {
        ScheduleDisplayBottomSheetViewModel(get())
    }
    viewModel{
        AddFriendFragmentViewModel(get(), get())
    }
    viewModel {
        FriendListFragmentViewModel(get(), get(), get(), get())
    }
    viewModel {
        FriendRequestFragmentViewModel(get(), get())
    }
    viewModel {
        VideoMenuFragmentViewModel(get(), get(), get())
    }
    viewModel {
        CreateGroupFragmentViewModel(get(), get())
    }
    viewModel {
        FriendRecommendationFragmentViewModel(get(), get())
    }
    viewModel {
        InputQuestionBottomSheetViewModel(get(), get(), get())
    }
    viewModel {
        SharedVideoPlayerViewModel()
    }
    viewModel {
        ChatMoreOptionsBottomSheetViewModel(get())
    }
    viewModel {
        EditGroupFragmentViewModel(get(), get(), get())
    }

    viewModel {
        FriendProfileFragmentViewModel(get(), get())
    }
}
