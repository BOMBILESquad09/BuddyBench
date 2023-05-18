package it.polito.mad.buddybench.persistence.firebaseRepositories

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import it.polito.mad.buddybench.classes.Profile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FriendRepository {
    val db = FirebaseFirestore.getInstance()

    suspend fun postFriendRequest(friendEmail: String) {
        withContext(Dispatchers.IO) {
            val currentEmail = Firebase.auth.currentUser!!.email!!
            val userDocument = db.collection("users").document(friendEmail)
                .update("friend_requests_pending", FieldValue.arrayUnion(db.document("users/$currentEmail")))
                .await()
        }
    }

    suspend fun acceptFriendRequest(friendEmail: String) {
        withContext(Dispatchers.IO) {
            val currentEmail = Firebase.auth.currentUser!!.email!!
            db.collection("users").document(currentEmail).update(
                mapOf("friend_requests_pending" to  FieldValue.arrayRemove(db.document("users/$friendEmail")),
                        "friends" to FieldValue.arrayUnion(db.document("users/$friendEmail"))
                    )).await()
            db.collection("users").document(friendEmail).update(
                "friends", FieldValue.arrayUnion(db.document("users/$currentEmail"))
            )
        }
    }

    suspend fun refuseFriendRequest(friendEmail: String) {
        withContext(Dispatchers.IO) {
            val currentEmail = Firebase.auth.currentUser!!.email!!
            db.collection("users").document(currentEmail).update(
                "friend_requests_pending" ,  FieldValue.arrayRemove(db.document("users/$friendEmail"))).await()
        }
    }
}

