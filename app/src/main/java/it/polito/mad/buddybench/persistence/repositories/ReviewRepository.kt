package it.polito.mad.buddybench.persistence.repositories

import it.polito.mad.buddybench.persistence.dao.CourtDao
import it.polito.mad.buddybench.persistence.dao.ReviewDao
import it.polito.mad.buddybench.persistence.dao.UserDao
import it.polito.mad.buddybench.persistence.dto.CourtDTO
import it.polito.mad.buddybench.persistence.dto.ReviewDTO
import it.polito.mad.buddybench.persistence.entities.Court
import it.polito.mad.buddybench.persistence.entities.Review
import it.polito.mad.buddybench.persistence.entities.toUserDTO
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReviewRepository @Inject constructor (
    private val reviewDao: ReviewDao,
    private val userDao: UserDao,
    private val courtDao: CourtDao
) {

    fun getAllByCourt(courtDTO: CourtDTO): List<ReviewDTO>{
        val reviews = reviewDao.getAllByCourt(courtDTO.name, courtDTO.sport)
        reviews.map { println("Repository reviews: id ${it.review.id} rating ${it.review.rating}") }
        return reviews.map {
            ReviewDTO(
                it.user.toUserDTO(),
                LocalDate.parse(it.review.date, DateTimeFormatter.ISO_LOCAL_DATE),
                it.review.rating,
                it.review.description,
                courtDTO
            )
        }
    }

    fun saveReview(reviewDTO: ReviewDTO): Boolean{
        val user = userDao.getUserByEmail(reviewDTO.user.email)!!.user
        val court = courtDao.getByNameAndSport(reviewDTO.courtDTO.name, reviewDTO.courtDTO.sport).court
        val review = reviewDao.getReview(court.id,user.id)


        // ** Insert new review
        if(review == null){
            println("INSERTING:")
            println(Review(
                description = reviewDTO.description,
                date = reviewDTO.date.format(DateTimeFormatter.ISO_LOCAL_DATE),
                rating = reviewDTO.rating,
                userId = user.id,
                courtId = court.id
            ))
            reviewDao.save(
                Review(
                    description = reviewDTO.description,
                    date = reviewDTO.date.format(DateTimeFormatter.ISO_LOCAL_DATE),
                    rating = reviewDTO.rating,
                    userId = user.id,
                    courtId = court.id
                )
            )
            reviewDao.updateRating(court.id, (court.nReviews + 1), (court.rating * court.nReviews + reviewDTO.rating) / (court.nReviews + 1))
        } else {

            // ** Update existing review
            println(review.copy(
                description = reviewDTO.description,
                date = reviewDTO.date.format(DateTimeFormatter.ISO_LOCAL_DATE),
                rating = reviewDTO.rating
            ))
            reviewDao.update(
                review.copy(
                    id = review.id,
                    description = reviewDTO.description,
                    date = reviewDTO.date.format(DateTimeFormatter.ISO_LOCAL_DATE),
                    rating = reviewDTO.rating,

                )
            ).let {
                println("updated $it")
                println(review.copy(
                    description = reviewDTO.description,
                    date = reviewDTO.date.format(DateTimeFormatter.ISO_LOCAL_DATE),
                    rating = reviewDTO.rating,
                ))
            }
            val updatedSumOfRatings = (court.rating * court.nReviews - review.rating)
            //reviewDao.updateRating(court.id, (court.nReviews), (updatedSumOfRatings + reviewDTO.rating) / (court.nReviews))
        }
        println(reviewDao.getAllByCourt(reviewDTO.courtDTO.name, reviewDTO.courtDTO.sport))
        return true
    }
}