package home.samples.quickcoffee.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MapViewModelFactory (private val mapViewModel: MapViewModel) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
            return mapViewModel as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}