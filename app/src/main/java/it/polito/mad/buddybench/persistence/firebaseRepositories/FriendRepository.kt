package it.polito.mad.buddybench.persistence.firebaseRepositories

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import it.polito.mad.buddybench.classes.Profile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FriendRepository {
    val db = FirebaseFirestore.getInstance()

    @Inject
    lateinit var userRepository: UserRepository

    var subscribed: Boolean = false
    fun subscribeFriends(onFailure: () -> Unit, onSuccess: () -> Unit) {
        if (subscribed) return
        val currentEmail = Firebase.auth.currentUser!!.email!!

        db.collection("users").document(currentEmail).addSnapshotListener { value, error ->

            if (value != null && value.exists() && error == null) {
                println(value.data.toString())
                onSuccess()
            } else if(error != null){
                onFailure()
            }
        }
        subscribed = true
    }


    /*ritorna la lista delle persone che non sono amiche*/
    suspend fun getNotFriends(onFailure: () -> Unit, onSuccess: () -> Unit): List<Profile> {
        return withContext(Dispatchers.IO) {
            try {
                val currentEmail = Firebase.auth.currentUser!!.email!!
                val usersDocuments = db.collection("users").get().await()
                val userFriends =
                    (usersDocuments.documents.find { it.id == currentEmail }!!.data!!["friends"] as List<DocumentReference>)
                val otherUsers =
                    usersDocuments.filter { !userFriends.map { f -> f.id }.contains(it.id) }.map {
                        UserRepository.serializeUser(it.data as Map<String, Object>)
                    }
                onSuccess()
                return@withContext otherUsers.filter { it.email != Firebase.auth.currentUser!!.email!! }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onFailure()
                }

            }
            listOf()
        }
    }

    /*manda una richiesta d'amicizia*/
    fun postFriendRequest(friendEmail: String, onFailure: () -> Unit, onSuccess: () -> Unit) {

        db.runTransaction { t ->

            val currentEmail = Firebase.auth.currentUser!!.email!!
            val userDocument = db.collection("users").document(friendEmail)
            t.update(
                userDocument,
                "friend_requests_pending",
                FieldValue.arrayUnion(db.document("users/$currentEmail"))
            )
            onSuccess()

        }.addOnFailureListener {
            onFailure()
        }


    }

    /*rimuovo un amico*/
    fun removeFriend(friendEmail: String, onFailure: () -> Unit, onSuccess: () -> Unit) {
        db.runTransaction { t ->


            val currentEmail = Firebase.auth.currentUser!!.email!!
            val currUserDoc = db.collection("users").document(currentEmail)
            val friendDoc = db.collection("users").document(friendEmail)

            t.update(
                currUserDoc,
                "friends", FieldValue.arrayRemove(db.document("users/$friendEmail"))
            )
            t.update(
                friendDoc,
                "friends", FieldValue.arrayRemove(db.document("users/$currentEmail"))
            )
            onSuccess()
        }

            .addOnFailureListener {
                onFailure()
            }

    }

    /*accetto una richiesta amico*/
    fun acceptFriendRequest(
        friendEmail: String,
        onFailure: () -> Unit,
        onSuccess: () -> Unit
    ) {

        db.runTransaction { t ->

            val currentEmail = Firebase.auth.currentUser!!.email!!
            val currUserDoc = db.collection("users").document(currentEmail)
            val friendDoc = db.collection("users").document(friendEmail)
            t.update(
                currUserDoc,
                mapOf(
                    "friend_requests_pending" to FieldValue.arrayRemove(db.document("users/$friendEmail")),
                    "friends" to FieldValue.arrayUnion(db.document("users/$friendEmail"))
                )
            )
            t.update(
                friendDoc,
                "friends", FieldValue.arrayUnion(db.document("users/$currentEmail"))
            )
            onSuccess()


        }.addOnFailureListener {
            onFailure()
        }

    }

    /*rifiuto una richiesta amico*/
    suspend fun refuseFriendRequest(
        friendEmail: String,
        onFailure: () -> Unit,
        onSuccess: () -> Unit
    ) {
        withContext(Dispatchers.IO) {
            try {
                val currentEmail = Firebase.auth.currentUser!!.email!!
                db.collection("users").document(currentEmail).update(
                    "friend_requests_pending",
                    FieldValue.arrayRemove(db.document("users/$friendEmail"))
                )
                    .await()
                onSuccess()
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onFailure()
                }
            }

        }
    }


    suspend fun removeFriendRequest(
        friendEmail: String,
        onFailure: () -> Unit,
        onSuccess: () -> Unit
    ) {
        withContext(Dispatchers.IO) {
            try {
                val currentEmail = Firebase.auth.currentUser!!.email!!
                db.collection("users").document(friendEmail).update(
                    "friend_requests_pending",
                    FieldValue.arrayRemove(db.document("users/$currentEmail"))
                )
                    .await()
                onSuccess()
            } catch (_: Exception) {
                withContext(Dispatchers.Main) {
                    onFailure()
                }
            }


        }
    }
}

