package it.polito.mad.buddybench.persistence.firebaseRepositories

import com.firebase.ui.auth.data.model.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import it.polito.mad.buddybench.classes.Profile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FriendRepository {
    val db = FirebaseFirestore.getInstance()

    /*ritorna la lista delle persone che non sono amiche*/
    suspend fun getNotFriends(): List<Profile>{
        return withContext(Dispatchers.IO){
            val currentEmail = Firebase.auth.currentUser!!.email!!
            val usersDocuments = db.collection("users").get().await()
            val userFriends = (usersDocuments.documents.find { it.id == currentEmail }!!.data!!["friends"] as List<DocumentReference>).map { it.id }
            val otherUsers = usersDocuments.filter { !userFriends.contains(it.id) }.map { UserRepository.serializeUser(it as Map<String, Object>) }
            otherUsers
        }
    }

    /*manda una richiesta d'amicizia*/
    suspend fun postFriendRequest(friendEmail: String) {
        withContext(Dispatchers.IO) {
            val currentEmail = Firebase.auth.currentUser!!.email!!
            val userDocument = db.collection("users").document(friendEmail)
                .update("friend_requests_pending", FieldValue.arrayUnion(db.document("users/$currentEmail")))
                .await()
        }
    }

    /*rimuovo un amico*/
    suspend fun removeFriend(friendEmail: String) {
        withContext(Dispatchers.IO) {
            val currentEmail = Firebase.auth.currentUser!!.email!!
            db.collection("users").document(currentEmail).update(
                    "friends" , FieldValue.arrayRemove(db.document("users/$friendEmail"))
                ).await()
            db.collection("users").document(friendEmail).update(
                "friends", FieldValue.arrayRemove(db.document("users/$currentEmail"))
            ).await()
        }
    }

    /*accetto una richiesta amico*/
    suspend fun acceptFriendRequest(friendEmail: String) {
        withContext(Dispatchers.IO) {
            val currentEmail = Firebase.auth.currentUser!!.email!!
            db.collection("users").document(currentEmail).update(
                mapOf("friend_requests_pending" to  FieldValue.arrayRemove(db.document("users/$friendEmail")),
                        "friends" to FieldValue.arrayUnion(db.document("users/$friendEmail"))
                    )).await()
            db.collection("users").document(friendEmail).update(
                "friends", FieldValue.arrayUnion(db.document("users/$currentEmail"))
            ).await()
        }
    }

    /*rifiuto una richiesta amico*/
    suspend fun refuseFriendRequest(friendEmail: String) {
        withContext(Dispatchers.IO) {
            val currentEmail = Firebase.auth.currentUser!!.email!!
            db.collection("users").document(currentEmail).update(
                "friend_requests_pending" ,  FieldValue.arrayRemove(db.document("users/$friendEmail")))
                .await()
        }
    }
}

