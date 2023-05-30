package it.polito.mad.buddybench.viewmodels

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.mad.buddybench.classes.Profile
import it.polito.mad.buddybench.persistence.firebaseRepositories.ImageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import kotlin.concurrent.thread

@HiltViewModel
class ImageViewModel @Inject constructor() : ViewModel() {


    private val imageRepository: ImageRepository = ImageRepository()

    private val _loading: MutableLiveData<Boolean> = MutableLiveData(true)
    val loading: LiveData<Boolean> = _loading
    var onFailure = {}
//    fun postUserImage(path: String, uri: Uri) {
//        runBlocking {
//            imageRepository.postUserImage(path, uri, {})
//        }
//    }

    fun postUserImage(path: String, uri: Uri, onFailure: () -> Unit, onSuccess: () -> Unit) {

        viewModelScope.launch {
            try {
                imageRepository.postUserImage(path, uri, onFailure, onSuccess)
                _loading.postValue(false)
            } catch (e: Exception) {
                onFailure()
            }
        }
    }

    fun postCourtImage(path: String, uri: Uri) {
        runBlocking {
            imageRepository.postCourtImage(path, uri)
        }
    }

    fun getUserImage(path: String, onFailure: () -> Unit, onSuccess: (Uri) -> Unit) {
        viewModelScope.launch {

                imageRepository.getUserImage(path,onFailure, onSuccess)


        }
    }

    fun getCourtImage(path: String, onFailure: () -> Unit, onSuccess: (Uri) -> Unit) {

        viewModelScope.launch {
                imageRepository.getCourtImage(path, onFailure ,onSuccess)

        }
    }

}