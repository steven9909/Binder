package com.example.binder.ui.usecase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import kotlinx.coroutines.Dispatchers
import repository.GoogleDriveRepository
import Result
import java.io.File

class UploadFileToGoogleDriveUseCase(private val googleDriveRepository: GoogleDriveRepository) :
    BaseUseCase<Triple<String?, String?, File>, Result<String?>>() {

    override val parameter: MutableLiveData<Triple<String?, String?, File>> = MutableLiveData()

    override val liveData: LiveData<Result<String?>> = parameter.switchMap {
        liveData(Dispatchers.IO) {
            emit(Result.loading(null))
            emit(googleDriveRepository.uploadFile(it.first, it.second, it.third))
        }
    }
}
