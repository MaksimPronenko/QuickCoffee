package home.samples.quickcoffee.models

data class OrderItem(
    // cafeId в программе не используется, но по сути, id кафе в заказе должен фигурировать.
    val cafeId: Int,
    val menuId: Int,
    val name: String,
    var quantity: Int,
    var priceSum: Int
)