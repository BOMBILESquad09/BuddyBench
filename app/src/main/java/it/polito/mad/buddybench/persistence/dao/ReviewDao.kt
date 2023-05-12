package it.polito.mad.buddybench.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import it.polito.mad.buddybench.persistence.entities.Review
import it.polito.mad.buddybench.persistence.entities.ReviewWithUser

@Dao
interface ReviewDao {

    @Query("SELECT * FROM review R, court C WHERE R.court = C.id and C.name = :courtName and C.sport = :sport")
    fun getAllByCourt(courtName: String, sport: String):List<ReviewWithUser>

    @Query("SELECT * FROM review")
    fun getAll():List<ReviewWithUser>

    @Query("SELECT * FROM review R WHERE R.court = :courtId and R.user = :userId")
    fun getReview(courtId: Int,  userId: Int): Review?

    @Insert
    fun save(reservation: Review)

    @Update(entity = Review::class, onConflict = OnConflictStrategy.REPLACE)
    fun update(reservation: Review)

    @Query("UPDATE Court SET n_reviews = :nReviews, rating = :rating WHERE id = :courtId")
    fun updateRating(courtId: Int, nReviews: Int, rating: Double)
}