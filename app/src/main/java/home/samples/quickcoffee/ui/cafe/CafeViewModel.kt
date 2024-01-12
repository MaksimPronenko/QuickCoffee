package home.samples.quickcoffee.ui.cafe

import android.annotation.SuppressLint
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
import home.samples.quickcoffee.App
import home.samples.quickcoffee.data.Repository
import home.samples.quickcoffee.models.CafeData
import home.samples.quickcoffee.models.CafeItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sqrt

private const val TAG = "CafeVM"

class CafeViewModel(
    private val repository: Repository,
    val application: App
) : ViewModel() {
    private val _state = MutableStateFlow<CafeVMState>(CafeVMState.Loading)
    val state = _state.asStateFlow()

    var token: String = ""
    var distancesCalculated = false
    private var cafeList: List<CafeData> = listOf()
    var cafeItemsList: List<CafeItem> = listOf()
    private val _cafeFlow = MutableStateFlow<List<CafeItem>>(emptyList())
    val cafeFlow = _cafeFlow.asStateFlow()
//    private val _cafeChannel = Channel<List<CafeItem>>()
//    val cafeChannel = _cafeChannel.receiveAsFlow()

    var latitudeCenterCurrent: Double? = null
    var longitudeCenterCurrent: Double? = null

    val fusedClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(application.applicationContext)

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            latitudeCenterCurrent = result.lastLocation?.latitude
            longitudeCenterCurrent = result.lastLocation?.longitude
            Log.d(
                TAG,
                "locationCallback: lat = $latitudeCenterCurrent, lon = $longitudeCenterCurrent"
            )
            cafeItemsList = createCafeItemsList()
            Log.d(TAG, cafeItemsList.toString())
            refreshCafesWithDistances()
//            _cafeFlow.value = cafeItemsList
//            _cafeChannel.send(element = cafeItemsList)

        }
    }

    fun refreshCafesWithDistances() {
        viewModelScope.launch(Dispatchers.IO) {
            _cafeFlow.value = cafeItemsList
//            _cafeChannel.send(element = cafeItemsList)
            _state.value = CafeVMState.DistancesLoaded
            distancesCalculated = true
        }
    }

    @SuppressLint("MissingPermission")
    fun startLocation() {
        Log.d(TAG, "Функция startLocation() запущена")
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 200)
            .apply {
                setWaitForAccurateLocation(false)
                setMinUpdateIntervalMillis(IMPLICIT_MIN_UPDATE_INTERVAL)
                setMaxUpdateDelayMillis(600)
            }.build()

        fusedClient.requestLocationUpdates(
            request,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    fun loadCafesLocations() {
        Log.d(TAG, "Функция loadCafe() запущена")
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = CafeVMState.Loading
            Log.d(TAG, "CafeVMState.Loading")

            cafeList = repository.getLocations(token) ?: emptyList()
            if (cafeList.isEmpty()) {
                Log.d(TAG, "CafeVMState.Error")
                _state.value = CafeVMState.Error
            } else {
                cafeItemsList = createCafeItemsList()
                Log.d(TAG, cafeItemsList.toString())
                _cafeFlow.value = cafeItemsList
                Log.d(TAG, "CafeVMState.Loaded")
                if (distancesCalculated) _state.value = CafeVMState.DistancesLoaded
                else _state.value = CafeVMState.Loaded
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
                    "${result.toInt()} км от вас"
                } else {
                    ""
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

    fun clearOrder() {
        application.order = listOf()
    }
}