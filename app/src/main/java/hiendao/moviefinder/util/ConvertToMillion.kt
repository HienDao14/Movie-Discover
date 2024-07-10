package hiendao.moviefinder.util

import java.text.DecimalFormat

fun Int.ConvertToMillion(): String {
    println(this)
    val df = DecimalFormat("#.#")
    if(this.toString().length > 9){
        val num = this.toFloat() / 1000000000
        val numFirst = num.toString().substringBefore(".")
        val numAfter = num.toString().substringAfter(".").take(2)
        return "$$numFirst.$numAfter Billion"
    }
    else if(this.toString().length > 6){
        val num = this.toFloat() / 10000000
        val numFirst = num.toString().substringBefore(".")
        val numAfter = num.toString().substringAfter(".").take(2)
        return "$$numFirst.$numAfter Million"
    } else if(this.toString().length > 3){
        val num = this.toFloat() / 1000
        val numFirst = num.toString().substringBefore(".")
        val numAfter = num.toString().substringAfter(".").take(2)
        return "$$numFirst.$numAfter Thousand"
    } else return "$${this}"
}