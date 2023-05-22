package it.polito.mad.buddybench.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.play.core.integrity.p
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.mad.buddybench.classes.Profile
import it.polito.mad.buddybench.persistence.firebaseRepositories.FriendRepository
import it.polito.mad.buddybench.persistence.firebaseRepositories.UserRepository
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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

    val mainScope = MainScope()


    fun subscribeFriendsList() {

        var init = true
        friendRepository.subscribeFriends({
        }) {

            mainScope.launch {
                if (init) {
                    getFriendsList()
                    getFriendRequests()
                    getPossibleFriends()
                    println("fine")
                    init = false
                } else {
                    refreshAll {}
                }
                _l.postValue(false)

            }
        }

    }

    fun refreshAll(onSuccess: () -> Unit) {
        _l.postValue(true)
        mainScope.launch {
            userRepository.fetchUser {
                getFriendRequests()
                getFriendsList()
                onSuccess()
            }
        }

        getPossibleFriends()
        _l.postValue(false)

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
        runBlocking {
            oldPossibleFriends = possibleFriends.value!!
            _possibleFriends.postValue(friendRepository.getNotFriends())
        }
    }


    fun refreshPossibleFriends(profile: Profile) {

        _possibleFriends.value = _possibleFriends.value!!.map {
            if (it.email == profile.email) {
                profile
            } else {
                it
            }
        }

    }

    private fun getFriendRequests() {
        if (currentUser != null) {
            runBlocking {
                userRepository.getUser(currentUser.email!!) {
                    oldFriendsRequests = _friendRequests.value!!
                    _friendRequests.postValue(it.pendings)
                }
            }

        }
    }

    fun confirmRequest(email: String, onSuccess: () -> Unit) {
        _lRequests.postValue(true)
        runBlocking {
            friendRepository.acceptFriendRequest(email)
            onSuccess()
            _lRequests.postValue(false)

        }
    }

    fun rejectRequest(email: String, onSuccess: () -> Unit) {
        _lRequests.postValue(true)
        runBlocking {
            friendRepository.refuseFriendRequest(email)
            _lRequests.postValue(false)
            onSuccess()

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
                if (p.email == email) {
                    p.isPending = true
                }
                p
            }
            _lAdd.postValue(false)
            callback()

        }
    }

    fun removeFriendRequest(email: String, onSuccess: () -> Unit) {
        runBlocking {

            friendRepository.removeFriendRequest(email)


            _possibleFriends.value = _possibleFriends.value!!.map {
                val p = it.copy()
                if (p.email == email) {
                    p.isPending = false
                }
                p
            }

            onSuccess()
        }
    }
}