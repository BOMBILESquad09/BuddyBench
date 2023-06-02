package it.polito.mad.buddybench.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.play.core.integrity.p
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.mad.buddybench.classes.Profile
import it.polito.mad.buddybench.persistence.dto.CourtDTO
import it.polito.mad.buddybench.persistence.dto.ReservationDTO
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
    private var allPossibleFriends: List<Profile> = listOf()

    var oldFriends = _friends.value!!
    val friends: LiveData<List<Profile>> get() = _friends
    var oldFriendsRequests = friendRequests.value!!
    val friendRequests: LiveData<List<Profile>> get() = _friendRequests

    var searchText = ""

    var onFailure: () -> Unit = {}
    lateinit var popNotification: (Profile) -> Unit


    fun subscribeFriendsList() {


        friendRepository.subscribeFriends({
            _lFriends.postValue(false)
            _lPossible.postValue(false)
            _lRequests.postValue(false)
        }) { val subscribed = friendRepository.subscribed
            _lFriends.value = !subscribed
            _lPossible.value = !subscribed
            _lRequests.value = !subscribed
            viewModelScope.launch {
                if (!subscribed) {
                    getFriendsList()
                    getFriendRequests()
                    getPossibleFriends()
                } else {
                    refreshAll {}
                }

            }
            friendRepository.subscribed = true

        }

    }

    fun refreshAll(onSuccess: () -> Unit) {
        viewModelScope.launch {
            userRepository.fetchUser(onFailure =
            {

                _lFriends.postValue(false)
                _lPossible.postValue(false)
                _lRequests.postValue(false)
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
            val freshPossibleFriends = friendRepository.getNotFriends(onFailure = {
                _lPossible.postValue(false)
                onFailure()
            }) {
                _lPossible.postValue(false)
            }
            allPossibleFriends = freshPossibleFriends

            _possibleFriends.postValue(applyFilterOnPossibleFriends(allPossibleFriends))
        }
    }

    private fun getFriendRequests() {

        if (currentUser != null) {
            runBlocking {
                userRepository.getUser(currentUser.email!!, onFailure = {
                    _lRequests.postValue(false)
                    onFailure()
                }) { it ->


                    if (oldFriendsRequests.size < (it.pendings.size)) {
                        //I need to send notifications only to very new one that I received
                        val freshRequestsEmail = it.pendings.map { it.email }
                        val oldRequestsEmail = oldFriendsRequests.map { it.email }
                        freshRequestsEmail.filter { !oldRequestsEmail.contains(it) }.map { fEmail ->
                            it.pendings.find { f -> fEmail == f.email }
                        }.forEach { p ->
                            popNotification(p!!)
                        }
                    }

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
        friendRepository.acceptFriendRequest(email, onFailure = onFailure) {
            onSuccess()
        }
    }

    fun rejectRequest(email: String, onSuccess: () -> Unit) {
        friendRepository.refuseFriendRequest(email, onFailure = onFailure) {
            onSuccess()
        }


    }

    fun removeFriend(email: String) {
        friendRepository.removeFriend(email, onFailure = onFailure) {

        }

    }

    fun sendRequest(email: String, onSuccess: () -> Unit) {
        friendRepository.postFriendRequest(email, onFailure = onFailure) {
            _possibleFriends.postValue(_possibleFriends.value!!.map {
                val p = it.copy()
                if (p.email == email) {
                    p.isPending = true
                }
                p
            })
            onSuccess()
        }
    }


    fun removeFriendRequest(email: String, onSuccess: () -> Unit) {

        friendRepository.removeFriendRequest(email, onFailure = onFailure) {
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

    private fun applyFilterOnPossibleFriends(profiles: List<Profile>): List<Profile> {
        return profiles.filter {
            (
                    it.location!!.contains(searchText, ignoreCase = true)
                            || it.nickname!!.contains(searchText, ignoreCase = true)
                            || it.name!!.contains(searchText, ignoreCase = true)
                            || it.surname!!.contains(searchText, ignoreCase = true)
                            || it.sports.any { sport ->
                        sport.name.toString().equals(searchText, ignoreCase = true)
                    }
                    )
        }
    }

    fun applyFilter() {
        _possibleFriends.value = allPossibleFriends.filter {
            (
                    it.location!!.contains(searchText, ignoreCase = true)
                            || it.nickname!!.contains(searchText, ignoreCase = true)
                            || it.name!!.contains(searchText, ignoreCase = true)
                            || it.surname!!.contains(searchText, ignoreCase = true)
                            || it.sports.any { sport ->
                        sport.name.toString().contains(searchText, ignoreCase = true)
                    }
                    )
        }
    }

}