package hiendao.moviefinder.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import hiendao.moviefinder.data.local.model.TvSeriesEntity

@Dao
interface TvSeriesDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTvSeries(tvSeries: TvSeriesEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListTvSeries(listTvSeries: List<TvSeriesEntity>)

    @Query("UPDATE tv_series SET addedToFavorite = :addedToFavorite, addedInFavoriteDate = CASE WHEN :addedToFavorite == 1 THEN :date ELSE \"\" END WHERE id = :seriesId")
    suspend fun changeFavorite(addedToFavorite: Int, date: String, seriesId: Int)

    @Query("SELECT * FROM tv_series WHERE category LIKE :category ORDER BY popularity DESC LIMIT 20 OFFSET :number ")
    fun loadCategoryTvSeries(category: String, number: Int): List<TvSeriesEntity>

    @Query("SELECT * FROM tv_series WHERE id = :id")
    suspend fun getTvSeriesById(id: Int) : TvSeriesEntity?

    @Query("SELECT * FROM tv_series WHERE addedToFavorite = 1 ORDER BY addedInFavoriteDate DESC LIMIT 20 OFFSET :number")
    fun getFavoriteTvSeries(number: Int): List<TvSeriesEntity>

    @Query("UPDATE tv_series SET credits = :credit WHERE id = :id")
    suspend fun updateCredits(credit: String, id: Int)
}