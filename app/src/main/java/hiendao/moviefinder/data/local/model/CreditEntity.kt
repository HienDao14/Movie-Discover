package hiendao.moviefinder.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

//Not done
@Entity(tableName = "credit")
data class CreditEntity(
    val adult: Boolean,
    val character: String,
    val gender: Int,
    @PrimaryKey
    val id: Int,
    val knownForDepartment: String,
    val name: String,
    val popularity: Double,
    val profilePath: String,
    val department: String,
    val job: String,
    val type: String,
    val movieId: String,
    val biography: String,
    val birthday: String,
    val deathday: String?,
    val homepage: String,
    val placeOfBirth: String,
    val externalIds: String,
    val addedInFavorite: Int = 0
)

// Movie -> List Credit -> Save CreditEntity -> Credit Detail -> Upsert CreditEntity ->
// MovieCredit -> List<MovieWithCast>
// MovieCast: List<MovieId>
// CharacterCast: List<String>
// Movie -> ListCredit -> Call DAO -> Get credit -> Credit -> List<MovieId> get index -> index get Character Cast
// Credit in domain ->