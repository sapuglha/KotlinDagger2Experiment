package com.robotsandpencils.kotlinexperiment.common

import com.robotsandpencils.kotlinexperiment.domain.common.Transformer
import io.reactivex.Observable
import io.reactivex.ObservableSource

class TestTransformer<T> : Transformer<T>() {
    override fun apply(upstream: Observable<T>): ObservableSource<T> {
        return upstream
    }
}
