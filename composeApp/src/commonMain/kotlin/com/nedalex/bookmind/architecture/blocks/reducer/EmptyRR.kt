package com.nedalex.bookmind.architecture.blocks.reducer

import com.nedalex.bookmind.architecture.blocks.model.EmptyResult

class EmptyRR<ViewState> : BaseReducer<ViewState, EmptyResult> {
    override fun reduce(viewState: ViewState, result: EmptyResult): ViewState = viewState
}