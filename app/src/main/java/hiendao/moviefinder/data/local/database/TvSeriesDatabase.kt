package hiendao.moviefinder.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import hiendao.moviefinder.data.local.dao.TvSeriesDAO
import hiendao.moviefinder.data.local.model.TvSeriesEntity

@Database(entities = [TvSeriesEntity::class], version = 2, exportSchema = false)

abstract class TvSeriesDatabase: RoomDatabase() {
    abstract val dao: TvSeriesDAO
}