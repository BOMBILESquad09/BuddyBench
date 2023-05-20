package it.polito.mad.buddybench.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.mad.buddybench.persistence.firebaseRepositories.ImageRepository
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class ImageViewModel @Inject constructor() : ViewModel(){

    val cacheCourtImages: HashMap<String, Uri> = HashMap()
    val cacheProfileImages: HashMap<String, Uri> = HashMap()



    val imageRepository = ImageRepository()

    fun postUserImage(path: String, uri: Uri){
        cacheProfileImages[path] = uri
        runBlocking {
            imageRepository.postUserImage(path, uri)
        }
    }

    fun postCourtImage(path: String, uri: Uri){
        cacheProfileImages[path] = uri
        runBlocking {
            imageRepository.postCourtImage(path, uri)
        }
    }

    fun getUserImage(path: String, onFailure: () ->Unit, onSuccess: (Uri)->Unit){
        if(cacheProfileImages[path] != null) {
            onSuccess(cacheProfileImages[path]!!)
            return
        }

        runBlocking {
            try{
                val image = imageRepository.getUserImage(path)
                cacheProfileImages[path] = image
                onSuccess(image)
            } catch (_: Exception){
                onFailure()
            }
        }
    }

    fun getCourtImage(path: String, onFailure:()->Unit, onSuccess: (Uri)->Unit){
        if(cacheCourtImages[path] != null) {
            onSuccess(cacheCourtImages[path]!!)
            return
        }

        runBlocking {
            try{
                val image = imageRepository.getCourtImage(path)
                cacheCourtImages[path] = image
                onSuccess(image)
            } catch (_: Exception){
            }
        }
    }

}