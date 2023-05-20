package it.polito.mad.buddybench.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.mad.buddybench.classes.Profile
import it.polito.mad.buddybench.persistence.firebaseRepositories.FriendRepository
import it.polito.mad.buddybench.persistence.firebaseRepositories.UserRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class FriendsViewModel @Inject constructor() : ViewModel() {

    // ** Repositories
    private val friendRepository: FriendRepository = FriendRepository()

    @Inject
    lateinit var userRepository: UserRepository

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

    var oldPossibleFriends = _possibleFriends.value!!
    val possibleFriends: LiveData<List<Profile>> get() = _possibleFriends
    var oldFriends = _friends.value!!
    val friends: LiveData<List<Profile>> get() = _friends
    var oldFriendsRequests = friendRequests.value!!
    val friendRequests: LiveData<List<Profile>> get() = _friendRequests


    private val subListener: MutableLiveData<Int> = MutableLiveData(0)

    fun subscribeFriendsList() {
        friendRepository.subscribeFriends({
        }) {
            subListener.postValue(subListener.value!! + 1)
        }
        subListener.observeForever {
            if (it == 0) {
                runBlocking {
                    getFriendsList()
                    getFriendRequests()
                    getPossibleFriends()
                }
            }
            if (it != 0) {
                runBlocking {
                    userRepository.fetchUser {
                        getFriendRequests()
                        getFriendsList()
                    }
                    getPossibleFriends()
                }
            }
        }
    }


    private fun getFriendsList() {
        runBlocking {
            userRepository.getUser {
                oldFriends = _friends.value!!

                _friends.postValue(it.friends)
            }
        }
    }

    private fun getPossibleFriends() {
        _l.postValue(true)
        runBlocking {
            oldPossibleFriends = possibleFriends.value!!

            _possibleFriends.postValue(friendRepository.getNotFriends())


            _l.postValue(false)
        }
    }



    private fun getFriendRequests() {
        _lRequests.postValue(true)
        if (currentUser != null) {
            runBlocking {
                userRepository.getUser(currentUser.email!!) {
                    oldFriendsRequests = _friendRequests.value!!
                    _friendRequests.postValue(it.pendings)
                }
                _lRequests.postValue(false)
            }

        }
    }

    fun confirmRequest(email: String) {
        _lRequests.postValue(true)
        runBlocking {
            friendRepository.acceptFriendRequest(email)
        }
        _lRequests.postValue(false)
    }

    fun rejectRequest(email: String) {
        _lRequests.postValue(true)
        runBlocking {
            friendRepository.refuseFriendRequest(email)
            _lRequests.postValue(false)
        }
    }

    fun removeFriend(email: String) {
        runBlocking {
            friendRepository.removeFriend(email)
        }
    }

    fun sendRequest(email: String, callback: () -> Unit) {
        _lAdd.postValue(true)
        runBlocking {

            friendRepository.postFriendRequest(email)
            _possibleFriends.value = _possibleFriends.value!!.map {
                val p = it.copy()
                if(p.email == email){
                    p.isPending = true
                }
                p
            }
            callback()
            _lAdd.postValue(false)
        }
    }

    fun removeFriendRequest(email: String, onSuccess: () -> Unit) {
        runBlocking {

            friendRepository.removeFriendRequest(email)


            _possibleFriends.value = _possibleFriends.value!!.map {
                val p = it.copy()
                if(p.email == email){
                    p.isPending = false
                }
                p
            }

            onSuccess()
        }
    }
}