package com.nedalex.presentation.architecture.reducer

import com.nedalex.presentation.architecture.model.EmptyResult


class EmptyRR<ViewState> : BaseReducer<ViewState, EmptyResult> {
    override fun reduce(viewState: ViewState, result: EmptyResult): ViewState = viewState
}