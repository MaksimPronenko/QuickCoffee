package home.samples.quickcoffee.ui.login

@Suppress("ConvertObjectToDataObject")
sealed class LoginVMState {
    object LoginProcess : LoginVMState()
    data class WorkingState(val emailState: Boolean?, val passwordState: Boolean?) : LoginVMState()
}