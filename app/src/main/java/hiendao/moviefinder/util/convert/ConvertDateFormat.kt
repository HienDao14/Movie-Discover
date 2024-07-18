package hiendao.moviefinder.util.convert

fun String.convertDateFormat(): String{
    val element = this.split("-")
    val month = element[1].convertToShortFormMonth()
    // y/m/d
    val result = "$month ${element[2]}, ${element[0]}"
    return result
}

fun String.convertToMonth(): String{
    return when (this.toInt()){
        1 -> "January"
        2 -> "February"
        3 -> "March"
        4 -> "April"
        5 -> "May"
        6 -> "June"
        7 -> "July"
        8 -> "August"
        9 -> "September"
        10 -> "October"
        11 -> "November"
        else -> "December"
    }
}

fun String.convertToShortFormMonth(): String {
    return when (this.toInt()){
        1 -> "Jan"
        2 -> "Feb"
        3 -> "Mar"
        4 -> "Apr"
        5 -> "May"
        6 -> "Jun"
        7 -> "Jul"
        8 -> "Aug"
        9 -> "Sep"
        10 -> "Oct"
        11 -> "Nov"
        else -> "Dec"
    }
}