package hiendao.moviefinder.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import hiendao.moviefinder.data.local.model.MovieEntity

@Database(entities = [MovieEntity::class], version = 3, exportSchema = false)
abstract class MovieDatabase: RoomDatabase() {
    abstract val dao: MovieDAO
}