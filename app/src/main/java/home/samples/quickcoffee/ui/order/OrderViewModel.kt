package home.samples.quickcoffee.ui.order

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import home.samples.quickcoffee.App
import home.samples.quickcoffee.models.OrderItem
import home.samples.quickcoffee.ui.ViewModelState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

private const val TAG = "OrderVM"

class OrderViewModel(
    val application: App
) : ViewModel() {
    private val _state = MutableStateFlow<ViewModelState>(ViewModelState.Loading)
    val state = _state.asStateFlow()

    var order: List<OrderItem> = listOf()
    private val _orderChannel = Channel<List<OrderItem>>()
    val orderChannel = _orderChannel.receiveAsFlow()

    fun loadOrder() {
        Log.d(TAG, "Функция loadOrder() запущена")
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = ViewModelState.Loading
            Log.d(TAG, "ViewModelState.Loading")
            order = application.order
            if (order.isEmpty()) {
                Log.d(TAG, "ViewModelState.Error")
                _state.value = ViewModelState.Error
            } else {
                Log.d(TAG, "ViewModelState.Loaded")
                _state.value = ViewModelState.Loaded
                _orderChannel.send(element = order)
            }
        }
    }

    fun onMinusClick(itemPosition: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d(TAG, "onMinusClick($itemPosition)")
            val itemToChange = order[itemPosition]
            val orderNew = order.toMutableList()
            if (itemToChange.quantity >= 2) {
                val priceOf1Item = itemToChange.priceSum / itemToChange.quantity
                itemToChange.quantity = itemToChange.quantity - 1
                itemToChange.priceSum = priceOf1Item * itemToChange.quantity
                Log.d(TAG, "orderNew[$itemPosition].quantity = ${orderNew[itemPosition].quantity}")
                orderNew[itemPosition] = itemToChange
                order = orderNew.toList()
                application.order = order
                _orderChannel.send(element = order)
            } else {
                orderNew.removeAt(itemPosition)
                order = orderNew.toList()
                application.order = order
                _orderChannel.send(element = order)
            }
        }
    }

    fun onPlusClick(itemPosition: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d(TAG, "onPlusClick($itemPosition)")
            val itemToChange = order[itemPosition]
            val orderNew = order.toMutableList()
            val priceOf1Item = itemToChange.priceSum / itemToChange.quantity
            itemToChange.quantity = itemToChange.quantity + 1
            itemToChange.priceSum = priceOf1Item * itemToChange.quantity
            orderNew[itemPosition] = itemToChange
            Log.d(TAG, "orderNew[$itemPosition].quantity = ${orderNew[itemPosition].quantity}")
            order = orderNew.toList()
            application.order = order
            _orderChannel.send(element = order)
        }
    }

    fun checkOrder(): Boolean {
        val result = application.order.isNotEmpty()
        Log.d(TAG, "application.order.size = ${application.order.size}")
        return result
    }
}