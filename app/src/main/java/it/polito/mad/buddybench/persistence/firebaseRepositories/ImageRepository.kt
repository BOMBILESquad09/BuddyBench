package it.polito.mad.buddybench.persistence.firebaseRepositories

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage

class ImageRepository {

    private val storage = FirebaseStorage.getInstance()
    private val profileImagesPath = "/profile_images"
    private val courtImagesPath = "/court_images"

    fun uploadImageToPath(fileUri: Uri, path: String, onSuccessListener: () -> Unit, onErrorListener: () -> Unit) {
        val storageRef = storage.getReference("$profileImagesPath/$path")
        storageRef.putFile(fileUri).addOnSuccessListener { onSuccessListener() }.addOnFailureListener { onErrorListener() }
    }

    fun getUserProfileImage(userId: String, destinationUri: Uri, onSuccessListener: (uri: Uri) -> Unit, onErrorListener: () -> Unit) {
        val storageRef = storage.getReference("$profileImagesPath/${userId}.jpg")
        storageRef.getFile(destinationUri).addOnSuccessListener { onSuccessListener(destinationUri) }.addOnFailureListener { onErrorListener() }
    }
}