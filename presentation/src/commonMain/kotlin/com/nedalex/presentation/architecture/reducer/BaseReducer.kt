package com.nedalex.presentation.architecture.reducer

/**
 * Reducer is a pure function that takes a current state and an action
 * and returns a new state.
 */
interface BaseReducer<ViewState, Result> {
    fun reduce(viewState: ViewState, result: Result): ViewState
}