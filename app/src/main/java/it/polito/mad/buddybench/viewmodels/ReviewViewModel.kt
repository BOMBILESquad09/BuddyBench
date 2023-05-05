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
    val reviews: LiveData<List<ReviewDTO>> get() = _reviews

    private val _l: MutableLiveData<Boolean> = MutableLiveData(false)
    private val l: LiveData<Boolean> = _l

    private val _loading: MutableLiveData<Boolean> = MutableLiveData(true)
    val loading: MutableLiveData<Boolean> = _loading


    fun getCourtReviews(name: String, sport: String): LiveData<List<ReviewDTO>>{
        _loading.value = true
        val court = courtRepository.getByNameAndSports(name, Sports.valueOf(sport))
        val reviewsList = reviewRepository.getAllByCourt(court)
        _reviews.postValue(reviewsList)
        _loading.postValue(false)
        return reviews
    }

    fun insertReview(review: ReviewDTO) {
        _l.value = true
        Thread{
            val result = reviewRepository.saveReview(review)
            _l.postValue(false)
        }.start()
    }
}