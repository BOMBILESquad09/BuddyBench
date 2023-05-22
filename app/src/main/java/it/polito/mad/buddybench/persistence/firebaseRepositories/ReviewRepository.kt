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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ReviewRepository {
    val db = FirebaseFirestore.getInstance()


    suspend fun getAllByCourt(courtDTO: CourtDTO, callback: (List<ReviewDTO>) -> Unit) {
        withContext(Dispatchers.IO) {
            val courtDocName = courtDTO.name.replace(" ", "_") + "_" + courtDTO.sport
            val res =
                db.collection("courts").document(courtDocName).collection("reviews").get().await()
            val reviews = mutableListOf<ReviewDTO>()
            val usersResponse = res.map { r -> db.collection("users").document(r.id).get() }
            val users =
                usersResponse.map { it.await() }.map { UserRepository.serializeUser(it.data!!) }
            for (r in res) {
                val user = users.find { u -> u.email == r.id }
                reviews.add(
                    ReviewDTO(
                        user!!,
                        LocalDate.parse(
                            r.data["date"] as String,
                            DateTimeFormatter.ISO_LOCAL_DATE
                        ),
                        (r.data["rating"] as Long).toInt(),
                        r.data["description"] as String,
                        courtDTO
                    )
                )
            }
            callback(reviews)

        }
    }


    /*val reviews = reviewDao.getAllByCourt(courtDTO.name, courtDTO.sport)
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
        val courtName = reviewDTO.courtDTO.name.replace(" ", "_") + "_" + reviewDTO.courtDTO.sport
        db.runTransaction { t ->
            val courtDoc = db.collection("courts").document(courtName)
            val court = t.get(courtDoc).toObject(CourtDTO::class.java)
            val reviewDoc = courtDoc.collection("reviews").document(reviewDTO.user.email)
            val review = t.get(reviewDoc)
            if (review.data == null) {
                val updates: Map<String, Any> = mapOf(
                    "nreviews" to court!!.nReviews + 1,
                    "rating" to ((court.rating * court.nReviews + reviewDTO.rating) / (court.nReviews + 1))
                )
                t.update(courtDoc, updates)
                t.set(
                    reviewDoc, mapOf(
                        "date" to reviewDTO.date.toString(),
                        "rating" to reviewDTO.rating,
                        "description" to reviewDTO.description
                    )
                )
            } else {
                val oldRating = (review.get("rating") as Long).toInt()
                val updates: Map<String, Any> = mapOf(
                    "rating" to ((court!!.rating * (court.nReviews) - oldRating + reviewDTO.rating) / court.nReviews)
                )
                t.update(courtDoc, updates)

            }
            callback()
        }

    }
}


