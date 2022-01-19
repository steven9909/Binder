package com.example.binder.ui.usecase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import repository.FirebaseRepository
import Result
import kotlinx.coroutines.Dispatchers
import repository.GoogleDriveRepository

class CreateGoogleDriveFolderUseCase(private val googleDriveRepository: GoogleDriveRepository) :
    BaseUseCase<String, Result<Boolean>>() {

    override val parameter: MutableLiveData<String> = MutableLiveData()

    override val liveData: LiveData<Result<Boolean>> = parameter.switchMap {
        liveData(Dispatchers.IO) {
            emit(Result.loading(null))
            val doesExist = googleDriveRepository.doesFolderExist(it)
            if (doesExist.data == false) {
                emit(googleDriveRepository.createFolder(it))
            } else {
                emit(doesExist)
            }
        }
    }
}