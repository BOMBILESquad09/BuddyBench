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



    suspend fun getUserImage(path: String, onSuccess:(Uri) -> Unit = {}){
        return withContext(Dispatchers.IO){
            val uri = storage.child("profile_images/$path").downloadUrl.await()
            withContext(Dispatchers.Main){
                onSuccess(uri)
            }

        }
    }

    suspend fun getCourtImage(path: String, onSuccess: (Uri) -> Unit){
        return withContext(Dispatchers.IO){

            val uri = storage.child("court_images/$path").downloadUrl.await()
            withContext(Dispatchers.Main){
                onSuccess(uri)
            }
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