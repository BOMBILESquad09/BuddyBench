package it.polito.mad.buddybench.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
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

    val mainScope = MainScope()
    @Inject
    lateinit var imageRepository: ImageRepository

    fun postUserImage(path: String, uri: Uri) {
        runBlocking {
            imageRepository.postUserImage(path, uri)
        }
    }

    fun postCourtImage(path: String, uri: Uri) {
        runBlocking {
            imageRepository.postCourtImage(path, uri)
        }
    }

    fun getUserImage(path: String, onFailure: () -> Unit, onSuccess: (Uri) -> Unit) {
        mainScope.launch {
            try{
                imageRepository.getUserImage(path, onSuccess)
            } catch (e:Exception){
                onFailure()
            }
        }
    }

    fun getCourtImage(path: String, onFailure: () -> Unit, onSuccess: (Uri) -> Unit) {

        mainScope.launch {
            try{
                imageRepository.getCourtImage(path, onSuccess)
            } catch (_: Exception) {
                onFailure()
            }
        }
    }

}