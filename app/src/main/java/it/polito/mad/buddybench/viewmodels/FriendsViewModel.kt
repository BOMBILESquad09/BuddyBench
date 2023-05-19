package it.polito.mad.buddybench.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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

    // ** Loading state
    private val _l: MutableLiveData<Boolean> = MutableLiveData(true)
    private val _lAdd: MutableLiveData<Boolean> = MutableLiveData(false)
    val l: LiveData<Boolean> get() = _l
    val lAdd: LiveData<Boolean> get() = _lAdd

    // ** Friends data
    private val _possibleFriends: MutableLiveData<List<Profile>> = MutableLiveData(emptyList())
    private val _friends: MutableLiveData<List<Profile>> = MutableLiveData(emptyList())
    // TODO: Friend requests

    val possibleFriends: LiveData<List<Profile>> get() = _possibleFriends
    val friends: LiveData<List<Profile>> get() = _friends

    fun getPossibleFriends() {
        _l.postValue(true)
        println("Loading")
        runBlocking {
            val list = friendRepository.getNotFriends()
            println("${list[0]}")
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
}