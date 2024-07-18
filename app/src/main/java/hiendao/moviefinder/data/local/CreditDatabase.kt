package hiendao.moviefinder.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import hiendao.moviefinder.data.local.model.CreditEntity

@Database(entities = [CreditEntity::class], version = 3, exportSchema = false)
abstract class CreditDatabase: RoomDatabase() {
    abstract val creditDao: CreditDAO
}