package it.polito.mad.buddybench.persistence.firebaseRepositories

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import it.polito.mad.buddybench.classes.ProfileData
import it.polito.mad.buddybench.persistence.dto.CourtDTO
import it.polito.mad.buddybench.persistence.dto.ReviewDTO
import it.polito.mad.buddybench.persistence.dto.UserDTO
import it.polito.mad.buddybench.persistence.entities.Review
import it.polito.mad.buddybench.persistence.entities.toUserDTO
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ReviewRepository {
    val db = FirebaseFirestore.getInstance()


    fun getAllByCourt(courtDTO: CourtDTO, callback: (List<ReviewDTO>) -> Unit) {
        val courtDocName = courtDTO.name.replace(" ", "_") + "_" + courtDTO.sport
        db.collection("users")
            .get()
            .addOnSuccessListener {
                val users = it.map { profile ->
                    val p = UserRepository.serializeUser(profile.data!! as Map<String, Object>)
                    UserDTO(
                        p.name!!,
                        p.surname!!,
                        p.nickname!!,
                        p.birthdate,
                        p.location!!,
                        p.email!!,
                        p.reliability!!,
                        p.imageUri.toString()
                    )
                }
                db.collection("courts")
                    .document(courtDocName)
                    .collection("reviews")
                    .get()
                    .addOnSuccessListener { reviewsDoc ->
                        val reviews = mutableListOf<ReviewDTO>()
                        for (rev in reviewsDoc) {
                            val user = users.find { it.email == (rev).id }
                            reviews.add(
                                ReviewDTO(
                                    user!!,
                                    LocalDate.parse(
                                        rev.data["date"] as String,
                                        DateTimeFormatter.ISO_LOCAL_DATE
                                    ),
                                    (rev.data["rating"] as Long).toInt(),
                                    rev.data["description"] as String,
                                    courtDTO
                                )
                            )
                        }
                        callback(reviews)
                    }
            }


    }


    /*val reviews = reviewDao.getAllByCourt(courtDTO.name, courtDTO.sport)
    reviews.map { println("Repository reviews: id ${it.review.id} rating ${it.review.rating}") }
    return reviews.map {
        ReviewDTO(
            it.user.toUserDTO(),
            LocalDate.parse(it.review.date, DateTimeFormatter.ISO_LOCAL_DATE),
            it.review.rating,
            it.review.description,
            courtDTO
        )
    }*/

    fun saveReview(reviewDTO: ReviewDTO, callback: () -> Unit) {
        val courtName =  reviewDTO.courtDTO.name.replace(" ", "_") + "_" + reviewDTO.courtDTO.sport

        val courtDoc = db.collection("courts").document(courtName)
        courtDoc.get()
            .addOnSuccessListener {
                val court = it.toObject(CourtDTO::class.java)

                val reviewDoc = courtDoc.collection("reviews").document(reviewDTO.user.email)
                reviewDoc.get()
                .addOnSuccessListener {
                        if(it.data == null){
                            val updates: Map<String, Any> = mapOf(
                                "nreviews" to court!!.nReviews +1,
                                "rating" to ((court.rating * court.nReviews + reviewDTO.rating) / court.nReviews +1)
                                )
                            courtDoc.update(
                                updates
                            ).addOnSuccessListener {
                                reviewDoc.set(mapOf("date" to reviewDTO.date.toString(),
                                        "rating" to reviewDTO.rating,
                                        "description" to reviewDTO.description
                                    )).addOnSuccessListener {
                                        callback()
                                }
                            }
                        } else{
                            val oldRating = (it.data!!.get("rating") as Long).toInt()
                            val updates: Map<String, Any> = mapOf(
                                "rating" to ((court!!.rating * (court.nReviews) - oldRating + reviewDTO.rating ) / court.nReviews)
                            )
                            courtDoc.update(updates).addOnSuccessListener{
                                reviewDoc.set(mapOf("date" to reviewDTO.date.toString(),
                                    "rating" to reviewDTO.rating,
                                    "description" to reviewDTO.description
                                )).addOnSuccessListener {
                                    callback()
                                }
                            }

                        }
                    }
            }




        /*val user = userDao.getUserByEmail(reviewDTO.user.email)!!.user
        val court = courtDao.getByNameAndSport(reviewDTO.courtDTO.name, reviewDTO.courtDTO.sport).court
        val review = reviewDao.getReview(court.id,user.id)


        // ** Insert new review
        if(review == null){
            println("INSERTING:")
            println(
                Review(
                description = reviewDTO.description,
                date = reviewDTO.date.format(DateTimeFormatter.ISO_LOCAL_DATE),
                rating = reviewDTO.rating,
                userId = user.id,
                courtId = court.id
            )
            )
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
            reviewDao.updateRating(court.id, (court.nReviews), (updatedSumOfRatings + reviewDTO.rating) / (court.nReviews))
        }
        return true*/
    }
}


