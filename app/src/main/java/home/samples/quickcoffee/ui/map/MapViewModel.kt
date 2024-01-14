package home.samples.quickcoffee.ui.map

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import home.samples.quickcoffee.data.Repository
import home.samples.quickcoffee.models.CafeData
import home.samples.quickcoffee.ui.ViewModelState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "MapVM"

class MapViewModel(
    private val repository: Repository
) : ViewModel() {

    private val _state = MutableStateFlow<ViewModelState>(
        ViewModelState.Loading
    )
    val state = _state.asStateFlow()

    var token: String = ""
    var cafeList: List<CafeData> = listOf()
    var latitudeCenter: Double? = null
    var longitudeCenter: Double? = null

    fun loadCafesLocations() {
        Log.d(TAG, "Функция loadCafesLocations() запущена")
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = ViewModelState.Loading
            Log.d(TAG, "ViewModelState.Loading")

            cafeList = repository.getLocations(token) ?: emptyList()
            if (cafeList.isEmpty()) {
                Log.d(TAG, "ViewModelState.Error")
                _state.value = ViewModelState.Error
            } else {
                Log.d(TAG, cafeList.toString())
                countCenterCoordinates()
                Log.d(TAG, "ViewModelState.Loaded")
                _state.value = ViewModelState.Loaded
            }
        }
    }

    private fun countCenterCoordinates() {
        val cafeListSize = cafeList.size
        var latitudeSum = 0.0
        var longitudeSum = 0.0
        cafeList.forEach {
            latitudeSum += it.point.latitude.toDouble()
            longitudeSum += it.point.longitude.toDouble()
        }
        latitudeCenter = latitudeSum / cafeListSize
        longitudeCenter = longitudeSum / cafeListSize
    }
}