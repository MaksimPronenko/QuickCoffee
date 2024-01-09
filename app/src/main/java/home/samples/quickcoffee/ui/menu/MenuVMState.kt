package home.samples.quickcoffee.ui.menu

sealed class MenuVMState {
    object Loading : MenuVMState()
    object Loaded : MenuVMState()
    object Error : MenuVMState()
}