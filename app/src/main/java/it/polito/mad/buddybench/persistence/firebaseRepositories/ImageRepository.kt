package it.polito.mad.buddybench.persistence.firebaseRepositories

import android.content.res.Resources.NotFoundException
import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ImageRepository {
    private val storage = FirebaseStorage.getInstance().reference
    private val cacheCourtImages: HashMap<String, Uri?> = HashMap()
    private val cacheProfileImages: HashMap<String, Uri?> = HashMap()



    suspend fun getUserImage(path: String): Uri{
        if(cacheProfileImages[path] != null) {
            return cacheProfileImages[path]!!
        }
        if(cacheProfileImages.keys.contains(path)) throw NotFoundException()

        return withContext(Dispatchers.IO){
            cacheProfileImages[path] = null
            cacheProfileImages[path] = storage.child("profile_images/$path").downloadUrl.await()
            cacheProfileImages[path]!!
        }
    }

    suspend fun getCourtImage(path: String): Uri{
        if(cacheCourtImages[path] != null) {
            return cacheCourtImages[path]!!
        }
        if(cacheCourtImages.keys.contains(path)) throw NotFoundException()
        return withContext(Dispatchers.IO){
            cacheCourtImages[path] = null
            cacheCourtImages[path] = storage.child("court_images/$path").downloadUrl.await()
            cacheCourtImages[path]!!

        }
    }

    suspend fun postCourtImage(path:String,image: Uri ){
        withContext(Dispatchers.IO){
            storage.child("court_images/$path").putFile(image).await()
        }
    }

    suspend fun postUserImage(path:String,image: Uri){
        withContext(Dispatchers.IO){
            storage.child("profile_images/$path").putFile(image).await()
        }
    }


}