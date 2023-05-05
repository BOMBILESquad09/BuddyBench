package it.polito.mad.buddybench.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.mad.buddybench.persistence.dto.CourtDTO
import it.polito.mad.buddybench.persistence.dto.ReviewDTO
import it.polito.mad.buddybench.persistence.repositories.CourtTimeRepository
import it.polito.mad.buddybench.persistence.repositories.ReviewRepository
import javax.inject.Inject

@HiltViewModel
class ReviewViewModel @Inject constructor(): ViewModel() {

    private lateinit var court: CourtDTO

    @Inject
    lateinit var reviewRepository: ReviewRepository

    private val _reviews: MutableLiveData<List<ReviewDTO>> = MutableLiveData(null)
    val reviews: LiveData<List<ReviewDTO>> = _reviews

    private val _l: MutableLiveData<Boolean> = MutableLiveData(false)
    private val l: LiveData<Boolean> = _l



    fun setCourt(court: CourtDTO){
        this.court = court
    }


    fun getCourtReviews(): LiveData<List<ReviewDTO>>{
        Thread{
            val reviewsList = reviewRepository.getAllByCourt(this.court)
            _reviews.postValue(reviewsList)



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
}