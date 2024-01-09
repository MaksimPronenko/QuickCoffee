package home.samples.quickcoffee.ui.cafe

sealed class CafeVMState {
    object Loading : CafeVMState()
    object Loaded : CafeVMState()
    object DistancesLoaded : CafeVMState()
    object Error : CafeVMState()
}