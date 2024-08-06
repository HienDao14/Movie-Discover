package hiendao.moviefinder.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import hiendao.moviefinder.data.local.model.MovieEntity
import hiendao.moviefinder.data.local.model.TvSeriesEntity

@Database(entities = [TvSeriesEntity::class], version = 1, exportSchema = false)

abstract class TvSeriesDatabase: RoomDatabase() {
    abstract val dao: TvSeriesDAO
}