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
                onSuccess()
            } else if (error != null) {
                onFailure()
            }
        }
    }


    /*ritorna la lista delle persone che non sono amiche*/
    suspend fun getNotFriends(onFailure: () -> Unit, onSuccess: () -> Unit): List<Profile> {
        return withContext(Dispatchers.IO) {
            try {
                val currentEmail = Firebase.auth.currentUser!!.email!!
                val currUserRequests = db.collection("users").document(currentEmail).get().await().data!!["friend_requests_pending"] as List<DocumentReference>
                val usersDocuments = db.collection("users").get().await()
                val userFriends =
                    (usersDocuments.documents.find { it.id == currentEmail }!!.data!!["friends"] as List<DocumentReference>)
                val otherUsers =
                    usersDocuments.filter { !userFriends.map { f -> f.id }.contains(it.id) }
                        .filter { !currUserRequests.map { fr -> fr.id }.contains(it.id) }
                        .map {
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

        }.addOnFailureListener {
            onFailure()
        }.addOnSuccessListener {
            onSuccess()
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
        }

            .addOnFailureListener {
                onFailure()
            }.addOnSuccessListener {
                onSuccess()

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


        }.addOnFailureListener {
            onFailure()
        }.addOnSuccessListener {
            onSuccess()

        }

    }

    /*rifiuto una richiesta amico*/
    fun refuseFriendRequest(
        friendEmail: String,
        onFailure: () -> Unit,
        onSuccess: () -> Unit
    ) {

        db.runTransaction { t ->
            val currentEmail = Firebase.auth.currentUser!!.email!!
            t.update(
                db.collection("users").document(currentEmail), "friend_requests_pending",
                FieldValue.arrayRemove(db.document("users/$friendEmail"))
            )
        }.addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener {
            onFailure()
        }

    }


    fun removeFriendRequest(
        friendEmail: String,
        onFailure: () -> Unit,
        onSuccess: () -> Unit
    ) {

        db.runTransaction { t ->
            val currentEmail = Firebase.auth.currentUser!!.email!!
            t.update(
                db.collection("users").document(friendEmail), "friend_requests_pending",
                FieldValue.arrayRemove(db.document("users/$currentEmail"))
            )
        }.addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener {
            onFailure()
        }
    }

}

