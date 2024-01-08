package home.samples.quickcoffee.ui.registration

@Suppress("ConvertObjectToDataObject")
sealed class RegistrationVMState {
    object RegistrationProcess : RegistrationVMState()
    data class WorkingState(val emailState: Boolean?, val password1State: Boolean?, val password2State: Boolean?) : RegistrationVMState()
    object NoPermission: RegistrationVMState()
}