package home.samples.quickcoffee.models

import java.math.BigDecimal

data class CafeData(
    val id: Int,
    val name: String,
    val point: Point
)

data class Point (
    val latitude: BigDecimal,
    val longitude: BigDecimal
)