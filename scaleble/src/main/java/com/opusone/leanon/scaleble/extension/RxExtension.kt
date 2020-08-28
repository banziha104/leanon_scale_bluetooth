package com.opusone.leanon.scaleble.extension

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

fun <T> Observable<T>.io() = this
    .subscribeOn(Schedulers.io())
    .observeOn(Schedulers.io())


fun <T> Single<T>.io() = this
    .subscribeOn(Schedulers.io())
    .observeOn(Schedulers.io())


fun Completable.io() = this
    .subscribeOn(Schedulers.io())
    .observeOn(Schedulers.io())


fun <T> Flowable<T>.io() = this
    .subscribeOn(Schedulers.io())
    .observeOn(Schedulers.io())

fun <T> Single<T>.justDoIt() = this.subscribe({},{it.printStackTrace()})