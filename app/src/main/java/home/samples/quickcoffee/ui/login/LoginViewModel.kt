package home.samples.quickcoffee.ui.login

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

private const val TAG = "LoginVM"

class LoginViewModel(
    private val repository: Repository
) : ViewModel() {
    private val _state = MutableStateFlow<LoginVMState>(
        LoginVMState.WorkingState(
            emailState = true,
            passwordState = true
        )
    )
    val state = _state.asStateFlow()

    private val _loginResult = Channel<Boolean>()
    val loginResult = _loginResult.receiveAsFlow()

    var email: String? = null
    var emailState: Boolean? = null

    var password: String? = null
    var passwordState: Boolean? = null

    var registrationResult: RegistrationResult? = null

    fun login() {
        Log.d(TAG, "Функция login() запущена")
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = LoginVMState.LoginProcess
            Log.d(TAG,"LoginVMState.LoginProcess")

            registrationResult = if (emailState == true && passwordState == true)
                repository.login(
                    RegistrationData(
                        email = email!!,
                        password = password!!
                    )
                )
            else null
            Log.d(TAG, registrationResult.toString())

            _state.value = LoginVMState.WorkingState(emailState, passwordState)
            Log.d(TAG, "LoginVMState.WorkingState")
            _loginResult.send(registrationResult == null)
        }
    }

    fun handleEnteredEmail() {
        Log.d(TAG, "handleEnteredEmail(): email = $email")
        emailState = email?.let { Patterns.EMAIL_ADDRESS.matcher(it).matches() } ?: false
        Log.d(TAG,"handleEnteredEmail(): emailState = $emailState")
        viewModelScope.launch {
            _state.value =
                LoginVMState.WorkingState(emailState, passwordState)
        }
    }

    fun handleEnteredPassword() {
        passwordState = !(password.isNullOrBlank())
        Log.d(TAG,"handleEnteredPasswords(): password = $password")
        viewModelScope.launch {
            _state.value =
                LoginVMState.WorkingState(emailState, passwordState)
        }
    }
}