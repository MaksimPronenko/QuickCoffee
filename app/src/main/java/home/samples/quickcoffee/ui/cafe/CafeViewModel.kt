package home.samples.quickcoffee.ui.cafe

import android.annotation.SuppressLint
import android.app.Application
import android.os.Looper
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.Builder.IMPLICIT_MIN_UPDATE_INTERVAL
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import home.samples.quickcoffee.data.Repository
import home.samples.quickcoffee.models.CafeData
import home.samples.quickcoffee.models.CafeItem
import home.samples.quickcoffee.ui.ViewModelState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sqrt

private const val TAG = "CafeVM"

class CafeViewModel(
    private val repository: Repository,
    application: Application
) : ViewModel() {
    private val _state = MutableStateFlow<ViewModelState>(ViewModelState.Loading)
    val state = _state.asStateFlow()

    var token: String? = null
    private var cafeList: List<CafeData> = listOf()
    var cafeItemsList: List<CafeItem> = listOf()
    private val _cafeFlow = MutableStateFlow<List<CafeItem>>(emptyList())
    val cafeFlow = _cafeFlow.asStateFlow()

    var firstLoading = true
    var latitudeCenterCurrent: Double? = null
    var longitudeCenterCurrent: Double? = null

    private val fusedClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(application.applicationContext)

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            latitudeCenterCurrent = result.lastLocation?.latitude
            longitudeCenterCurrent = result.lastLocation?.longitude
//            if (latitudeCenterCurrent == null || longitudeCenterCurrent == null) {
//                _state.value = ViewModelState.Error
//            } else {
//                if (firstLoading) {
//                    Log.d(TAG, "firstLoading = true - Состояние Active включается.")
//                    _state.value = ViewModelState.Loaded
//                }
//            }
        }
    }

    @SuppressLint("MissingPermission")
    fun startLocation() {
//        val request = LocationRequest.create()
//            .setInterval(1_000)
//            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
            .apply {
                setWaitForAccurateLocation(false)
                setMinUpdateIntervalMillis(IMPLICIT_MIN_UPDATE_INTERVAL)
                setMaxUpdateDelayMillis(100000)
            }.build()

        fusedClient.requestLocationUpdates(
            request,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    fun loadCafesLocations(token: String?) {
        Log.d(TAG, "Функция loadCafe() запущена")
        startLocation()
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = ViewModelState.Loading
            Log.d(TAG, "ViewModelState.Loading")

            if (token != null) cafeList = repository.getLocations(token) ?: emptyList()
            if (cafeList.isEmpty()) {
                Log.d(TAG, "ViewModelState.Error")
                _state.value = ViewModelState.Error
            } else {
                cafeItemsList = createCafeItemsList()
                Log.d(TAG, cafeItemsList.toString())
                _cafeFlow.value = cafeItemsList
                Log.d(TAG, "ViewModelState.Loaded")
                _state.value = ViewModelState.Loaded
            }
        }
    }

    private fun createCafeItemsList(): List<CafeItem> {
        val cafes = mutableListOf<CafeItem>()
        cafeList.forEach {
            val cafeLatitude = it.point.latitude.toDouble()
            val cafeLongitude = it.point.longitude.toDouble()
            val distanceText =
                if (latitudeCenterCurrent != null && longitudeCenterCurrent != null) {
                    val result = 111.2 * sqrt(
                        (longitudeCenterCurrent!! - cafeLongitude) * (longitudeCenterCurrent!! - cafeLongitude) + (latitudeCenterCurrent!! - cafeLatitude) * cos(
                            Math.PI * longitudeCenterCurrent!! / 180
                        ) * (latitudeCenterCurrent!! - cafeLatitude) * cos(
                            Math.PI * cafeLongitude / 180
                        )
                    )
                    "$result км от вас"
                } else {
                    "$latitudeCenterCurrent - $cafeLatitude; $longitudeCenterCurrent - $cafeLongitude"
                }
            cafes.add(
                CafeItem(
                    id = it.id,
                    name = it.name,
                    distance = distanceText
                )
            )
        }
        return cafes.toList()
    }
}