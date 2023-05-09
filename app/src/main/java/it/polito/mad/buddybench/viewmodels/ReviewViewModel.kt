package it.polito.mad.buddybench.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.mad.buddybench.enums.Sports
import it.polito.mad.buddybench.persistence.dto.CourtDTO
import it.polito.mad.buddybench.persistence.dto.ReviewDTO
import it.polito.mad.buddybench.persistence.repositories.CourtRepository
import it.polito.mad.buddybench.persistence.repositories.ReviewRepository
import javax.inject.Inject

@HiltViewModel
class ReviewViewModel @Inject constructor(): ViewModel() {

    @Inject
    lateinit var reviewRepository: ReviewRepository

    @Inject
    lateinit var courtRepository: CourtRepository

    private val _reviews: MutableLiveData<List<ReviewDTO>> = MutableLiveData(listOf())
    private val _court: MutableLiveData<CourtDTO> = MutableLiveData()

    val reviews: LiveData<List<ReviewDTO>> get() = _reviews
    val court: LiveData<CourtDTO> get() = _court

    private var _check: Boolean = false
    val check = _check
    private val _l: MutableLiveData<Boolean> = MutableLiveData(true)
    val l: LiveData<Boolean> = _l

    fun getCourtReviews(name: String, sport: String): LiveData<List<ReviewDTO>>{
        _l.postValue(true)
        Thread{
            val court = courtRepository.getByNameAndSports(name, Sports.valueOf(sport))
            val reviewsList = reviewRepository.getAllByCourt(court)

            //val playedFlag = courtRepository.checkIfPlayed(name, sport, profile.email )
            //_check = playedFlag
            _reviews.postValue(reviewsList)
            _l.postValue(false)
        }.start()
        return reviews
    }

    fun insertReview(review: ReviewDTO) {
        _l.value = true
        Thread{
            val result = reviewRepository.saveReview(review)
            _l.postValue(false)
        }.start()
    }

    fun getCourt(name: String, sport: String): LiveData<CourtDTO> {
        _l.postValue(true)
        Thread {
            val court = courtRepository.getByNameAndSports(name, Sports.valueOf(sport))
            _court.postValue(court)
            _l.postValue(false)
        }.start()
        return _court
    }
}