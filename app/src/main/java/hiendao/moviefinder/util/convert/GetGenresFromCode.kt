package hiendao.moviefinder.util.convert

import hiendao.moviefinder.domain.model.Genres

fun getGenresFromCode(codes: List<Int>): List<Genres>{
    val genres = mutableListOf<Genres>()
    codes.forEach {code ->
        genres.add(fromCode(code))
    }
    return genres
}

fun fromCode(code: Int): Genres{
    return when(code){
        28 -> Genres(28, "Action")
        12 -> Genres(12, "Adventure")
        16 -> Genres(16, "Animation")
        35 -> Genres(35, "Comedy")
        80 -> Genres(80, "Crime")
        99 -> Genres(99, "Documentary")
        18 -> Genres(18, "Drama")
        10751 -> Genres(10751, "Family")
        14 -> Genres(14, "Fantasy")
        36 -> Genres(36, "History")
        27 -> Genres(27, "Horror")
        10402 -> Genres(10402, "Music")
        9648 -> Genres(9648, "Mystery")
        10749 -> Genres(10749, "Romance")
        878 -> Genres(878, "Science Fiction")
        10770 -> Genres(10770, "TV Movie")
        53 -> Genres(53, "Thriller")
        10752 -> Genres(10752, "War")
        else -> Genres(37, "Western")
    }
}
