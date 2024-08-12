package hiendao.moviefinder.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import hiendao.moviefinder.data.local.dao.MovieDAO
import hiendao.moviefinder.data.local.model.MovieEntity

@Database(entities = [MovieEntity::class], version = 6, exportSchema = false)
abstract class MovieDatabase: RoomDatabase() {
    abstract val dao: MovieDAO
}