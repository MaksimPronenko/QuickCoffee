package home.samples.quickcoffee.ui.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MenuViewModelFactory (private val menuViewModel: MenuViewModel) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MenuViewModel::class.java)) {
            return menuViewModel as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}