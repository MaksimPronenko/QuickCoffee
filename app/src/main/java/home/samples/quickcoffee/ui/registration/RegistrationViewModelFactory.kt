package home.samples.quickcoffee.ui.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class RegistrationViewModelFactory (private val registrationViewModel: RegistrationViewModel) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegistrationViewModel::class.java)) {
            return registrationViewModel as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}