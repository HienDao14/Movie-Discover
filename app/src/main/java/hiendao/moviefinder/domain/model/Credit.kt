package hiendao.moviefinder.domain.model

data class Credit(
    val adult: Boolean,
    val character: List<String>,
    val gender: Int,
    val id: Int,
    val knownForDepartment: String,
    val name: String,
    val popularity: Double,
    val profilePath: String,
    val department: String,
    val job: String,
    val type: String,
    val movieId: List<String>,
    val biography: String,
    val birthday: String,
    val deathday: String?,
    val homepage: String?,
    val placeOfBirth: String,
    val externalIds: List<String>,
    val addedInFavorite: Boolean
)