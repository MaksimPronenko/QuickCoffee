package home.samples.quickcoffee

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import home.samples.quickcoffee.models.OrderItem
import javax.inject.Inject

@HiltAndroidApp
class App @Inject constructor() : Application() {
    var order: List<OrderItem> = listOf()
}