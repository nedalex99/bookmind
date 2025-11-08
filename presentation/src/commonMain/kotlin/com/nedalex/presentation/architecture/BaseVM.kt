package com.nedalex.presentation.architecture

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nedalex.presentation.architecture.reducer.BaseReducer
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

abstract class BaseVM<ViewState, Result, Action, NavigationEvent>(
    viewState: ViewState,
    private val reducer: BaseReducer<ViewState, Result>
) : ViewModel() {
    private val _viewState: MutableState<ViewState> = mutableStateOf(viewState)

    val viewState: ViewState
        get() = _viewState.value

    private val _navigation = MutableSharedFlow<NavigationEvent>()
    val navigation = _navigation.asSharedFlow()

    /**
     * Should be the only entry point to viewModel from the view.
     * @param action object with/without data can be passed to distinguish what kind of action it is.
     */
    abstract fun onAction(action: Action)

    /**
     * Pass result object which is ready to be applied to view state.
     */
    protected fun onResult(result: Result) {
        _viewState.value = reducer.reduce(_viewState.value, result)
    }

    protected fun navigate(event: NavigationEvent) {
        viewModelScope.launch {
            _navigation.emit(event)
        }
    }
}