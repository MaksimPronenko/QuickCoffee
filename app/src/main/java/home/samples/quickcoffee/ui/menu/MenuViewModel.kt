package home.samples.quickcoffee.ui.menu

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import home.samples.quickcoffee.App
import home.samples.quickcoffee.data.Repository
import home.samples.quickcoffee.models.MenuData
import home.samples.quickcoffee.models.MenuItem
import home.samples.quickcoffee.models.OrderItem
import home.samples.quickcoffee.ui.ViewModelState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

private const val TAG = "MenuVM"

class MenuViewModel(
    private val repository: Repository,
    val application: App
) : ViewModel() {
    private val _state = MutableStateFlow<ViewModelState>(ViewModelState.Loading)
    var state = _state.asStateFlow()

    var token: String = ""
    private var cafeId: Int = 0
    private var menuDataList: List<MenuData> = listOf()
    var menu: List<MenuItem> = listOf()
    private val _menuFlow = MutableStateFlow<List<MenuItem>>(emptyList())
    val menuFlow = _menuFlow.asStateFlow()
    private val _menuChannel = Channel<List<MenuItem>>()
    val menuChannel = _menuChannel.receiveAsFlow()
    private var loadMenuJob: Job? = null

    private fun loadCafeMenu() {
        Log.d(TAG, "Функция loadCafeMenu() запущена, cafeId = $cafeId")
        Log.d(TAG, "loadMenuJob = ${loadMenuJob?.isActive == true}")
        loadMenuJob = viewModelScope.launch(Dispatchers.IO) {
            _state.value = ViewModelState.Loading
            Log.d(TAG, "viewModelScope - loadCafeMenu(), cafeId = $cafeId")
            Log.d(TAG, "MenuVMState.Loading")
            menuDataList = repository.getCafeMenu(token, cafeId) ?: emptyList()
            if (menuDataList.isEmpty()) {
                Log.d(TAG, "MenuVMState.Error")
                _state.value = ViewModelState.Error
            } else {
                menu = convertMenuDataListToMenuItemList(menuDataList)
                Log.d(TAG, "MenuVMState.Loaded")
                _state.value = ViewModelState.Loaded
                _menuFlow.value = menu
            }
        }
    }

    private fun convertMenuDataListToMenuItemList(menuDataList: List<MenuData>): List<MenuItem> {
        Log.d(TAG, "Функция convertMenuDataListToMenuItemList() запущена")
        val menuItemListMutable: MutableList<MenuItem> = mutableListOf()
        menuDataList.forEach {
            menuItemListMutable.add(
                MenuItem(
                    id = it.id,
                    name = it.name,
                    imageURL = it.imageURL,
                    quantity = 0,
                    price = it.price
                )
            )
        }
        return menuItemListMutable.toList()
    }

    fun onMinusClick(itemPosition: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d(TAG, "onMinusClick($itemPosition)")
            val itemToChange = menu[itemPosition]
            if (itemToChange.quantity >= 1) {
                itemToChange.quantity = itemToChange.quantity - 1
                val menuNew = menu.toMutableList()
                menuNew[itemPosition] = itemToChange
                Log.d(TAG, "menuNew[$itemPosition].quantity = ${menuNew[itemPosition].quantity}")
                menu = menuNew.toList()
                _menuChannel.send(element = menu)
            }
        }
    }

    fun onPlusClick(itemPosition: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d(TAG, "onPlusClick($itemPosition)")
            val itemToChange = menu[itemPosition]
            itemToChange.quantity = itemToChange.quantity + 1
            val menuNew = menu.toMutableList()
            menuNew[itemPosition] = itemToChange
            Log.d(TAG, "menuNew[$itemPosition].quantity = ${menuNew[itemPosition].quantity}")
            menu = menuNew.toList()
            _menuChannel.send(element = menu)
        }
    }

    fun payment(): Boolean {
        val orderMutable: MutableList<OrderItem> = mutableListOf()
        menu.forEach {
            if (it.quantity >= 1) {
                orderMutable.add(
                    OrderItem(
                        cafeId = cafeId,
                        menuId = it.id,
                        name = it.name,
                        quantity = it.quantity,
                        priceSum = it.price * it.quantity
                    )
                )
            }
        }
        application.order = orderMutable.toList()
        return application.order.isNotEmpty()
    }

    fun checkCafeId(newCafeId: Int) {
        Log.d(TAG, "checkCafeId($newCafeId) запущена")
        if (newCafeId != cafeId && newCafeId != 0) {
            cafeId = newCafeId
            menu = emptyList()
            application.order = emptyList()
            loadCafeMenu()
        }
    }
}