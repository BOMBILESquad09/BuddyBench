package it.polito.mad.buddybench.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.mad.buddybench.classes.Profile
import it.polito.mad.buddybench.persistence.firebaseRepositories.FriendRepository
import it.polito.mad.buddybench.persistence.firebaseRepositories.UserRepository
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class FriendsViewModel @Inject constructor() : ViewModel() {

    // ** Repositories
    private val friendRepository: FriendRepository = FriendRepository()
    private val userRepository: UserRepository = UserRepository()

    // ** Current user
    private val currentUser = FirebaseAuth.getInstance().currentUser

    // ** Loading state
    private val _l: MutableLiveData<Boolean> = MutableLiveData(true)
    private val _lAdd: MutableLiveData<Boolean> = MutableLiveData(false)
    private val _lRequests: MutableLiveData<Boolean> = MutableLiveData(false)
    val l: LiveData<Boolean> get() = _l
    val lAdd: LiveData<Boolean> get() = _lAdd
    val lRequests: LiveData<Boolean> get() = _lRequests

    // ** Friends data
    private val _possibleFriends: MutableLiveData<List<Profile>> = MutableLiveData(emptyList())
    private val _friends: MutableLiveData<List<Profile>> = MutableLiveData(emptyList())
    private val _friendRequests: MutableLiveData<List<Profile>> = MutableLiveData(emptyList())

    val possibleFriends: LiveData<List<Profile>> get() = _possibleFriends
    val friends: LiveData<List<Profile>> get() = _friends
    val friendRequests: LiveData<List<Profile>> get() = _friendRequests

    fun getPossibleFriends() {
        _l.postValue(true)
        println("Loading")
        runBlocking {
            val list = friendRepository.getNotFriends().filter { it.email != currentUser!!.email  }
            _possibleFriends.postValue(list)
        }
        println("Done")
        _l.postValue(false)
    }

    fun sendRequest(email: String, callback: () -> Unit) {
        _lAdd.postValue(true)
        runBlocking {
            friendRepository.postFriendRequest(email)
        }
        _lAdd.postValue(false)
        callback()
    }

    fun getFriendRequests() {
        _lRequests.postValue(true)
        if (currentUser != null) {
            runBlocking {
                userRepository.getUser(currentUser.email!!) {
                    _friendRequests.postValue(it.pendings)
                }
            }
        }
        _lRequests.postValue(false)
    }

    fun confirmRequest(email: String) {
        _lRequests.postValue(true)
        runBlocking { friendRepository.acceptFriendRequest(email) }
        _lRequests.postValue(false)
    }

    fun rejectRequest(email: String) {
        _lRequests.postValue(true)
        runBlocking { friendRepository.refuseFriendRequest(email) }
        _lRequests.postValue(false)
    }
}