package hiendao.moviefinder.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import hiendao.moviefinder.data.local.dao.CreditDAO
import hiendao.moviefinder.data.local.model.CreditEntity

@Database(entities = [CreditEntity::class], version = 4, exportSchema = false)
abstract class CreditDatabase: RoomDatabase() {
    abstract val creditDao: CreditDAO
}