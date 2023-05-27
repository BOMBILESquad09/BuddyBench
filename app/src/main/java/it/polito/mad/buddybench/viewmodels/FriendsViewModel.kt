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
    private val _l: MutableLiveData<Boolean> = MutableLiveData(false)
    private val _lPossible: MutableLiveData<Boolean> = MutableLiveData(false)
    private val _lFriends: MutableLiveData<Boolean> = MutableLiveData(false)
    private val _lAdd: MutableLiveData<Boolean> = MutableLiveData(false)
    private val _lRequests: MutableLiveData<Boolean> = MutableLiveData(false)
    val l: LiveData<Boolean> get() = _l
    val lFriends: LiveData<Boolean> get() = _lFriends
    val lPossible: LiveData<Boolean> get() = _lPossible


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
    var onFailure: () -> Unit = {}
    var init = true

    fun subscribeFriendsList() {

        _lFriends.value = true
        _lPossible.value = true
        _lRequests.value = true
        friendRepository.subscribeFriends({
            _lFriends.postValue(false)
            _lPossible.postValue(false)
            _lRequests.postValue( false)
        }) {

            mainScope.launch {
                if (init) {
                    getFriendsList()
                    getFriendRequests()
                    getPossibleFriends()
                    init = false
                } else {
                    refreshAll {}
                }
            }
        }

    }

    fun refreshAll(onSuccess: () -> Unit) {
        mainScope.launch {
            userRepository.fetchUser(onFailure =
            {
                onFailure()
                onSuccess()
            }
            ) {
                getFriendRequests()
                getFriendsList()
                getPossibleFriends()
                onSuccess()

            }

        }
    }


    private fun getFriendsList() {
        runBlocking {
            userRepository.getUser(onFailure = {
                _lFriends.postValue(false)
                onFailure()
            }) {
                _lFriends.postValue(false)

                oldFriends = _friends.value!!
                _friends.postValue(it.friends)

            }
        }
    }

    private fun getPossibleFriends() {
        runBlocking {
            oldPossibleFriends = possibleFriends.value!!
            _possibleFriends.postValue(friendRepository.getNotFriends(onFailure = {
                _lPossible.postValue(false)
                onFailure() }){
                _lPossible.postValue(false)

            }
            )
        }
    }

    private fun getFriendRequests() {
        if (currentUser != null) {
            runBlocking {
                userRepository.getUser(currentUser.email!!,onFailure = {
                    onFailure() }) {


                    oldFriendsRequests = _friendRequests.value!!

                    _lRequests.postValue(false)
                    _friendRequests.postValue(it.pendings)
                }
            }

        }
    }


    fun refreshPossibleFriends(profile: Profile) {
        _possibleFriends.value = _possibleFriends.value!!.map {
            if (it.email == profile.email) {
                profile.copy()
            } else {
                it
            }
        }

    }



    fun confirmRequest(email: String, onSuccess: () -> Unit) {
        mainScope.launch {
            friendRepository.acceptFriendRequest(email,onFailure = onFailure){
                onSuccess()
            }

        }
    }

    fun rejectRequest(email: String, onSuccess: () -> Unit) {
        runBlocking {
            friendRepository.refuseFriendRequest(email,onFailure = onFailure){
                onSuccess()
            }


        }
    }

    fun removeFriend(email: String) {
        mainScope.launch {
            friendRepository.removeFriend(email,onFailure = onFailure){

            }
        }
    }

    fun sendRequest(email: String, onSuccess: () -> Unit) {
        mainScope.launch {
            friendRepository.postFriendRequest(email,onFailure = onFailure){
                _possibleFriends.postValue( _possibleFriends.value!!.map {
                    val p = it.copy()
                    if (p.email == email) {
                        p.isPending = true
                    }
                    p
                })
                onSuccess()
            }
        }
    }

    fun removeFriendRequest(email: String, onSuccess: () -> Unit) {
        mainScope.launch {

            friendRepository.removeFriendRequest(email,onFailure = onFailure){

            }


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