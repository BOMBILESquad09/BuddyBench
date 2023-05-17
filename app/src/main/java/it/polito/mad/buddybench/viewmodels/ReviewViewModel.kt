package it.polito.mad.buddybench.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.enums.Sports
import it.polito.mad.buddybench.persistence.dto.CourtDTO
import it.polito.mad.buddybench.persistence.dto.ReviewDTO
import it.polito.mad.buddybench.persistence.repositories.CourtRepository
import it.polito.mad.buddybench.persistence.repositories.ReviewRepository
import it.polito.mad.buddybench.persistence.repositories.UserRepository
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ReviewViewModel @Inject constructor(): ViewModel() {

    @Inject
    lateinit var reviewRepository: ReviewRepository

    val reviewRepositoryFirebase = it.polito.mad.buddybench.persistence.firebaseRepositories.ReviewRepository()

    @Inject
    lateinit var courtRepository: CourtRepository

    val courtRepositoryFirebase = it.polito.mad.buddybench.persistence.firebaseRepositories.CourtRepository()

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

    private val _l: MutableLiveData<Boolean> = MutableLiveData(true)
    val l: LiveData<Boolean> = _l

    fun getCourtReviews(name: String, sport: String, context: Context): LiveData<List<ReviewDTO>>{
        _l.postValue(true)
        val sharedPreferences = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        val currentUser = userRepository.getCurrentUser(sharedPreferences)
        courtRepositoryFirebase.getCourt(name,sport){
            _court.postValue(it)
            reviewRepositoryFirebase.getAllByCourt(it){ reviewsDocs ->
                if (reviewsDocs.any {r -> r.user.email == currentUser.email }) {
                    val userReview = reviewsDocs.first { r -> r.user.email == currentUser.email }
                    _userReview.postValue(userReview)
                }
                val reviews = reviewsDocs.filter { r -> r.user.email != currentUser.email }
                _reviews.postValue(reviews)
                _l.postValue(false)
            }
        }



        return reviews
    }

    fun insertReview(description: String, rating: Int, context: Context): LiveData<ReviewDTO> {
        _l.value = true


        val sharedPreferences = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        val currentUser = userRepository.getCurrentUser(sharedPreferences)
        val review = ReviewDTO(currentUser, LocalDate.now(), rating, description, court.value!!)
        reviewRepositoryFirebase.saveReview(review){
            _userReview.postValue(review)
            courtRepositoryFirebase.getCourt(_court.value!!.name, _court.value!!.sport){
                c ->
                _court.postValue(c)
                _l.postValue(false)
            }
        }


        return _userReview
    }

    fun userCanReview(name: String, sport: String, context: Context): LiveData<Boolean> {
        _l.postValue(true)
        val sharedPreferences = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        val currentUser = userRepository.getCurrentUser(sharedPreferences)
        courtRepositoryFirebase.checkIfPlayed(name, sport, currentUser.email){
            println(it)
            println("-------------------")
            _canReview.postValue(it)
            _l.postValue(false)
        }

        return _canReview
    }
}