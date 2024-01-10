package home.samples.quickcoffee.data

import android.util.Log
import home.samples.quickcoffee.api.retrofit
import home.samples.quickcoffee.models.CafeData
import home.samples.quickcoffee.models.MenuData
import home.samples.quickcoffee.models.RegistrationData
import home.samples.quickcoffee.models.RegistrationResult
import javax.inject.Inject

private const val TAG = "CoffeeRepository"

class Repository @Inject constructor() {

    suspend fun register(registrationData: RegistrationData): RegistrationResult? {
        kotlin.runCatching {
            retrofit.register(registrationData)
        }.fold(
            onSuccess = {
                Log.d(TAG, it.toString())
                return it
            },
            onFailure = {
                Log.d(TAG, "Failure")
                return null
            }
        )
    }

    suspend fun login(registrationData: RegistrationData): RegistrationResult? {
        kotlin.runCatching {
            retrofit.login(registrationData)
        }.fold(
            onSuccess = {
                Log.d(TAG, it.toString())
                return it
            },
            onFailure = {
                Log.d(TAG, "Failure")
                return null
            }
        )
    }

    suspend fun getLocations(token: String): List<CafeData>? {
        Log.d(TAG, "token = $token")
        val bearerToken = "Bearer $token"
        kotlin.runCatching {
            retrofit.getLocations(bearerToken)
        }.fold(
            onSuccess = {
                Log.d(TAG, it.toString())
                return it
            },
            onFailure = {
                Log.d(TAG, "Failure")
                return null
            }
        )
    }

    suspend fun getCafeMenu(token: String, id: Int): List<MenuData>? {
        Log.d(TAG, "token = $token, id = $id")
        val bearerToken = "Bearer $token"
        kotlin.runCatching {
            retrofit.getCafeMenu(bearerToken, id)
        }.fold(
            onSuccess = {
                Log.d(TAG, it.toString())
                return it
            },
            onFailure = {
                Log.d(TAG, "Failure")
                return null
            }
        )
    }
}