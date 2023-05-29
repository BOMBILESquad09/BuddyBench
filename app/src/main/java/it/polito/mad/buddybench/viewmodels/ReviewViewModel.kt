package it.polito.mad.buddybench.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.classes.Profile
import it.polito.mad.buddybench.enums.Sports
import it.polito.mad.buddybench.persistence.dto.CourtDTO
import it.polito.mad.buddybench.persistence.dto.ReviewDTO
import it.polito.mad.buddybench.persistence.firebaseRepositories.CourtRepository
import it.polito.mad.buddybench.persistence.firebaseRepositories.ReviewRepository
import it.polito.mad.buddybench.persistence.firebaseRepositories.UserRepository
import kotlinx.coroutines.launch

import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ReviewViewModel @Inject constructor() : ViewModel() {

    private val reviewRepository = ReviewRepository()
    private val courtRepository = CourtRepository()

    @Inject
    lateinit var userRepository: UserRepository


    private val _reviews: MutableLiveData<List<ReviewDTO>> = MutableLiveData(listOf())
    private val _court: MutableLiveData<CourtDTO> = MutableLiveData()
    private val _canReview: MutableLiveData<Boolean> = MutableLiveData()
    private val _userReview: MutableLiveData<ReviewDTO> = MutableLiveData(null)

    val reviews: LiveData<List<ReviewDTO>> get() = _reviews
    val court: LiveData<CourtDTO> get() = _court
    val canReview: LiveData<Boolean> get() = _canReview
    val userReview: LiveData<ReviewDTO> get() = _userReview

    private val _l: MutableLiveData<Boolean> = MutableLiveData(null)
    val l: LiveData<Boolean> = _l

    var onFailure = {}

    fun getCourtReviews(name: String, sport: String): LiveData<List<ReviewDTO>> {
        _l.postValue(true)
        viewModelScope.launch {
            val currentUser = Firebase.auth.currentUser!!.email!!
            lateinit var courtFetched: CourtDTO

            courtRepository.getCourt(name, sport, onFailure) {
                courtFetched = it
                _court.postValue(it)
            }

            reviewRepository.getAllByCourt(courtFetched, onFailure) { reviewsDocs ->
                if (reviewsDocs.any { r -> r.user.email == currentUser }) {
                    val userReview = reviewsDocs.first { r -> r.user.email == currentUser }
                    _canReview.postValue(true)
                    _userReview.postValue(userReview)
                    _l.postValue(false)
                } else {
                    userCanReview(name, sport)
                }
                val reviews = reviewsDocs.filter { r -> r.user.email != currentUser }
                _reviews.postValue(reviews)
                _l.postValue(false)

            }
        }
        return reviews
    }


    fun insertReview(
        description: String,
        rating: Int,
        onSuccess: (Boolean) -> Unit
    ): LiveData<ReviewDTO> {

        val profile = Profile.mockProfile()
        profile.email = Firebase.auth.currentUser!!.email!!
        val review = ReviewDTO(profile, LocalDate.now(), rating, description, court.value!!)
        reviewRepository.saveReview(review, onFailure) {
            val pair: Pair<Double, Int> = if (userReview.value == null) {
                val newNReviews = court.value!!.nReviews + 1
                val newRating =
                    (court.value!!.rating * court.value!!.nReviews + rating) / newNReviews
                Pair(newRating, newNReviews)
            } else {
                val newRating =
                    (court.value!!.rating * court.value!!.nReviews - userReview.value!!.rating + rating) / court.value!!.nReviews
                Pair(newRating, court.value!!.nReviews)
            }
            _userReview.postValue(review)
            val updatedCourt = _court.value!!.copy()
            updatedCourt.nReviews = pair.second
            updatedCourt.rating = pair.first
            _court.postValue(updatedCourt)
            onSuccess(it)
        }


        return _userReview
    }

    private fun userCanReview(name: String, sport: String): LiveData<Boolean> {
        _l.postValue(true)
        val currentUser = Firebase.auth.currentUser!!.email!!
        courtRepository.checkIfPlayed(name, sport, currentUser, onFailure = onFailure) {
            _canReview.postValue(it)
            _l.postValue(false)
        }
        return _canReview
    }
}