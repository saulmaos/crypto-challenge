package com.example.cryptochallenge.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import io.reactivex.disposables.CompositeDisposable

@InstallIn(ActivityComponent::class)
@Module
class MainViewModelModule {

    @Provides
    fun provideCompositeDisposable(): CompositeDisposable =
        CompositeDisposable()
}