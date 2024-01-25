package home.samples.quickcoffee.ui.cafe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class CafeViewModelFactory (private val cafeViewModel: CafeViewModel) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CafeViewModel::class.java)) {
            return cafeViewModel as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}