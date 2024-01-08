package home.samples.quickcoffee.ui.registration

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import home.samples.quickcoffee.data.Repository
import home.samples.quickcoffee.models.RegistrationData
import home.samples.quickcoffee.models.RegistrationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

private const val TAG = "RegistrationVM"

class RegistrationViewModel(
    private val repository: Repository
) : ViewModel() {

//    var permissionsGranted = false

    private val _state = MutableStateFlow<RegistrationVMState>(
        RegistrationVMState.WorkingState(
            emailState = true,
            password1State = true,
            password2State = true
        )
    )
    val state = _state.asStateFlow()

    private val _registrationError = Channel<Boolean>()
    val registrationError = _registrationError.receiveAsFlow()

    var email: String? = null
    var emailState: Boolean? = null

    var password1: String? = null
    var password1State: Boolean? = null

    var password2: String? = null
    var password2State: Boolean? = null

    var registrationResult: RegistrationResult? = null

    fun register() {
        Log.d(TAG, "Функция register() запущена")
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = RegistrationVMState.RegistrationProcess
            Log.d(TAG, "RegistrationVMState.RegistrationProcess")

            registrationResult = if (emailState == true && password1State == true)
                repository.register(
                    RegistrationData(
                        email = email!!,
                        password = password1!!
                    )
                )
            else null
            Log.d(TAG, registrationResult.toString())

            _state.value =
                RegistrationVMState.WorkingState(emailState, password1State, password2State)
            Log.d(TAG, "RegistrationVMState.WorkingState")
            _registrationError.send(registrationResult == null)
        }
    }

    fun handleEnteredEmail() {
        Log.d(TAG, "handleEnteredEmail(): email = $email")
        emailState = email?.let { Patterns.EMAIL_ADDRESS.matcher(it).matches() } ?: false
        Log.d(TAG, "handleEnteredEmail(): emailState = $emailState")
        viewModelScope.launch {
            _state.value =
                RegistrationVMState.WorkingState(emailState, password1State, password2State)
        }
    }

    fun handleEnteredPasswords() {
        password1State = !(password1.isNullOrBlank()) && password1 == password2
        password2State = !(password2.isNullOrBlank()) && password1 == password2
        Log.d(TAG, "handleEnteredPasswords(): password1 = $password1; password2 = $password2")
        viewModelScope.launch {
            _state.value =
                RegistrationVMState.WorkingState(emailState, password1State, password2State)
        }
    }

    fun handlePermissionsStateChange(permissionsGranted: Boolean) {
        viewModelScope.launch {
            if (permissionsGranted) {
                _state.value =
                    RegistrationVMState.WorkingState(emailState, password1State, password2State)
            } else {
                _state.value =
                    RegistrationVMState.NoPermission
            }
        }
    }
}