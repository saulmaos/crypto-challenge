package com.example.cryptochallenge.utils

import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean

data class Event<out T>(private val content: T) {
    private var hasBeenHandled = AtomicBoolean(false)

    fun getIfNotHandled(): T? = if (hasBeenHandled.getAndSet(true)) null else content

    @Suppress("unused")
    fun peek(): T = content
}

class EventObserver<T>(private val onEventUnhandledContent: (T) -> Unit) : Observer<Event<T>> {
    override fun onChanged(event: Event<T>?) {
        event?.getIfNotHandled()?.let { value ->
            onEventUnhandledContent(value)
        }
    }
}
