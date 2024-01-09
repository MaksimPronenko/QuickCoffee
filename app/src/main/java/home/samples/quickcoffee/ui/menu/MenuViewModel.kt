package home.samples.quickcoffee.ui.menu

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import home.samples.quickcoffee.data.Repository
import home.samples.quickcoffee.models.MenuItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "MenuVM"

class MenuViewModel(
    private val repository: Repository
) : ViewModel() {
    private val _state = MutableStateFlow<MenuVMState>(MenuVMState.Loading)
    val state = _state.asStateFlow()

    var token: String? = null
    var id: Int? = null
    var menu: List<MenuItem> = listOf()
    private val _menuFlow = MutableStateFlow<List<MenuItem>>(emptyList())
    val menuFlow = _menuFlow.asStateFlow()

    fun loadCafeMenu() {
        Log.d(TAG, "Функция loadCafeMenu() запущена")
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = MenuVMState.Loading
            Log.d(TAG, "MenuVMState.Loading")

            if (token != null && id != null) menu = repository.getCafeMenu(token!!, id!!) ?: emptyList()
            if (menu.isEmpty()) {
                Log.d(TAG, "MenuVMState.Error")
                _state.value = MenuVMState.Error
            } else {
                _menuFlow.value = menu
                Log.d(TAG, "MenuVMState.Loaded")
                _state.value = MenuVMState.Loaded
            }
        }
    }
}