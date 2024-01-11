package home.samples.quickcoffee.ui.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class OrderViewModelFactory (private val orderViewModel: OrderViewModel) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OrderViewModel::class.java)) {
            return orderViewModel as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}