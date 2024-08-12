package hiendao.moviefinder.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import hiendao.moviefinder.data.local.model.CreditEntity

@Dao
interface CreditDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun upsertListCredits(credits: List<CreditEntity>)

    @Upsert
    suspend fun upsertCredit(creditEntity: CreditEntity)

    @Query("UPDATE credit SET addedInFavorite = :favorite, addedInFavoriteDate = CASE WHEN :favorite == 1 THEN :addedDate ELSE \"\" END WHERE id = :creditId")
    suspend fun changeFavoriteCredit(favorite: Int, addedDate: String, creditId: Int)

    @Query("UPDATE credit SET character = :characters, movieId = :movieIds WHERE id = :creditId")
    suspend fun insertCredit(characters: String, movieIds: String, creditId : Int)

    @Query("SELECT * FROM credit WHERE movieId LIKE :movieId ORDER BY popularity DESC")
    suspend fun getCredits(movieId: String): List<CreditEntity>

    @Query("SELECT * FROM credit WHERE id = :creditId")
    suspend fun getCredit(creditId: Int): CreditEntity?

    @Query("SELECT * FROM credit WHERE addedInFavorite = 1 ORDER BY addedInFavoriteDate DESC LIMIT 20 OFFSET :number")
    suspend fun getFavoriteCredits(number: Int): List<CreditEntity>

}