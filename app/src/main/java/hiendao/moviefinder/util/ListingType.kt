package hiendao.moviefinder.util

import hiendao.moviefinder.domain.model.Genres

enum class ListingType {
    GRID,
    COLUMN
}

enum class FilterType {
    TRENDING_DAY,
    TRENDING_WEEK
}

data class DataItemInDiscover(
    val id: Int? = null,
    val name: String,
    val apiQuery: String? = null
)

val listGenresInDiscover = listOf(
    DataItemInDiscover(id = -1, name = "All genres"),
    DataItemInDiscover(id = 28,name =  "Action"),
    DataItemInDiscover(id = 12,name =  "Adventure"),
    DataItemInDiscover(id = 16,name =  "Animation"),
    DataItemInDiscover(id = 35,name =  "Comedy"),
    DataItemInDiscover(id = 80,name =  "Crime"),
    DataItemInDiscover(id = 99,name =  "Documentary"),
    DataItemInDiscover(id = 18,name =  "Drama"),
    DataItemInDiscover(id = 10751,name =  "Family"),
    DataItemInDiscover(id = 14,name =  "Fantasy"),
    DataItemInDiscover(id = 36,name =  "History"),
    DataItemInDiscover(id = 27,name =  "Horror"),
    DataItemInDiscover(id = 10402,name =  "Music"),
    DataItemInDiscover(id = 9648,name =  "Mystery"),
    DataItemInDiscover(id = 10749,name =  "Romance"),
    DataItemInDiscover(id = 878,name =  "Science Fiction"),
    DataItemInDiscover(id = 10770,name =  "TV Movie"),
    DataItemInDiscover(id = 53,name =  "Thriller"),
    DataItemInDiscover(id = 10752,name =  "War"),
    DataItemInDiscover(id = 37,name =  "Western")
)

val listCatalogInDiscover = listOf(
    DataItemInDiscover(null, "Popular", "popularity.desc"),
    DataItemInDiscover(null, "New", "primary_release_date.desc"),
    DataItemInDiscover(null, "Featured", "vote_average.desc"),
    DataItemInDiscover(null, "Revenue", "revenue.desc")
)

val listCatalogTvSeriesInDiscover = listOf(
    DataItemInDiscover(null, "Popular", "popularity.desc"),
    DataItemInDiscover(null, "New", "first_air_date.desc"),
    DataItemInDiscover(null, "Featured", "vote_average.desc")
)

val listTypeInDiscover = listOf(
    DataItemInDiscover(null, "Movie", null),
    DataItemInDiscover(null, "Series", null)
)
