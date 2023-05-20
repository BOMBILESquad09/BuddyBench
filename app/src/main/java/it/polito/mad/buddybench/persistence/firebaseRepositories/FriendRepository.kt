package it.polito.mad.buddybench.persistence.firebaseRepositories

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import it.polito.mad.buddybench.classes.Profile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FriendRepository {
    val db = FirebaseFirestore.getInstance()

    fun subscribeFriends(onFailure: () -> Unit, onSuccess: () -> Unit) {
        val currentEmail = Firebase.auth.currentUser!!.email!!
        db.collection("users").document(currentEmail).addSnapshotListener { value, error ->
            if (value != null && value.exists() && error == null) {
                onSuccess()
            } else {
                onFailure()
            }
        }
    }


    /*ritorna la lista delle persone che non sono amiche*/
    suspend fun getNotFriends(): List<Profile> {
        return withContext(Dispatchers.IO) {
            val currentEmail = Firebase.auth.currentUser!!.email!!
            val usersDocuments = db.collection("users").get().await()
            val userFriends =
                (usersDocuments.documents.find { it.id == currentEmail }!!.data!!["friends"] as List<DocumentReference>)
            val otherUsers =
                usersDocuments.filter { !userFriends.map { f -> f.id }.contains(it.id) }.map {
                    UserRepository.serializeUser(it.data as Map<String, Object>)
                }

            otherUsers.filter { it.email != Firebase.auth.currentUser!!.email!! }
        }
    }

    /*manda una richiesta d'amicizia*/
    suspend fun postFriendRequest(friendEmail: String) {
        withContext(Dispatchers.IO) {
            val currentEmail = Firebase.auth.currentUser!!.email!!
            val userDocument = db.collection("users").document(friendEmail)
                .update(
                    "friend_requests_pending",
                    FieldValue.arrayUnion(db.document("users/$currentEmail"))
                )
                .await()
        }
    }

    /*rimuovo un amico*/
    suspend fun removeFriend(friendEmail: String) {
        db.runTransaction { t ->
            suspend fun transaction() {

                val currentEmail = Firebase.auth.currentUser!!.email!!
                val currUserDoc = db.collection("users").document(currentEmail)
                val friendDoc = db.collection("users").document(friendEmail)
                withContext(Dispatchers.IO) {
                    t.update(
                        currUserDoc,
                        "friends", FieldValue.arrayRemove(db.document("users/$friendEmail"))
                    )
                    t.update(
                        friendDoc,
                        "friends", FieldValue.arrayRemove(db.document("users/$currentEmail"))
                    )
                }
            }

            runBlocking { transaction() }
        }

    }

    /*accetto una richiesta amico*/
    suspend fun acceptFriendRequest(friendEmail: String) {
        db.runTransaction { t ->
            runBlocking {
                suspend {
                    withContext(Dispatchers.IO) {
                        val currentEmail = Firebase.auth.currentUser!!.email!!
                        val currUserDoc  = db.collection("users").document(currentEmail)
                        val friendDoc = db.collection("users").document(friendEmail)
                        t.update(currUserDoc,
                            mapOf(
                                "friend_requests_pending" to FieldValue.arrayRemove(db.document("users/$friendEmail")),
                                "friends" to FieldValue.arrayUnion(db.document("users/$friendEmail"))
                            )
                        )
                        t.update(friendDoc,
                            "friends", FieldValue.arrayUnion(db.document("users/$currentEmail"))
                        )
                    }
                }.invoke()
            }

        }

    }

    /*rifiuto una richiesta amico*/
    suspend fun refuseFriendRequest(friendEmail: String) {
        withContext(Dispatchers.IO) {
            val currentEmail = Firebase.auth.currentUser!!.email!!
            db.collection("users").document(currentEmail).update(
                "friend_requests_pending", FieldValue.arrayRemove(db.document("users/$friendEmail"))
            )
                .await()
        }
    }
}

