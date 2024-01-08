package home.samples.quickcoffee.ui

sealed class ViewModelState {
    object Loading : ViewModelState()
    object Loaded : ViewModelState()
    object Error : ViewModelState()
}