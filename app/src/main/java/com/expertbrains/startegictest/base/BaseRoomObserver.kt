package com.expertbrains.startegictest.base

import io.reactivex.Observer
import io.reactivex.disposables.Disposable

abstract class BaseRoomObserver<T : Any> : Observer<T> {
    override fun onComplete() {}

    override fun onSubscribe(d: Disposable) {}

    override fun onNext(t: T) {
        onSuccess(t)
    }

    override fun onError(e: Throwable) {
        onFail(e)
    }

    protected abstract fun onSuccess(response: T)
    protected abstract fun onFail(errorMessage: Throwable)
}