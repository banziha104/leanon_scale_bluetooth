package com.opusone.leanon.scaleble.extension

import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

// 자동 제거 등록
//operator fun AutoClearedDisposableContract.plusAssign(disposable: Disposable) = this.add(disposable)
//
//operator fun com.opusone.leanon.reactivex.activity.AutoClearedDisposable.plusAssign(disposable: Disposable)
//        = this.add(disposable)
//
//operator fun AutoClearedDisposable.plusAssign(disposable: Disposable)
//        = this.add(disposable)

operator fun CompositeDisposable.plusAssign(disposable: Disposable) {
    this.add(disposable)
}

fun runOnIoScheduler(func: () -> Unit): Disposable
        = Completable.fromCallable(func).subscribeOn(Schedulers.io()).subscribe()
