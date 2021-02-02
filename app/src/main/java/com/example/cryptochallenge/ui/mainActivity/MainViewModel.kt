package com.example.cryptochallenge.ui.mainActivity

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.cryptochallenge.R
import com.example.cryptochallenge.data.model.Book
import com.example.cryptochallenge.data.repository.BitsoRepository
import com.example.cryptochallenge.utils.Event

class MainViewModel(
    private val bitsoRepository: BitsoRepository
): ViewModel() {

    companion object {
        const val INTENT_BOOK = "intent_book"
    }

    private val _events = MutableLiveData<Event<MainNavigation>>()
    val events: LiveData<Event<MainNavigation>> = _events

    val books: LiveData<Event<BooksResponse>> = Transformations.map(bitsoRepository.availableBooks) {
        _events.value = Event(MainNavigation.HideBooksListLoading)
        when(it) {
            is BitsoRepository.NetworkResponse.Success<List<Book>> -> {
                Event(BooksResponse.BooksList(it.data))
            }
            is BitsoRepository.NetworkResponse.Error -> {
                Event(BooksResponse.Error(it.errorId))
            }
        }
    }

    fun onInternetChange(isConnected: Boolean) {
        if (isConnected) {
            _events.value = Event(MainNavigation.ShowBooksListLoading)
            bitsoRepository.requestAvailableBooks()
        } else {
            _events.value = Event(MainNavigation.NoInternet(R.string.error_no_internet))
        }
    }

    fun onReloadPressed() {
        onInternetChange(true)
    }

    sealed class BooksResponse {
        data class BooksList(val books: List<Book>): BooksResponse()
        data class Error(@StringRes val errorId: Int): BooksResponse()
    }

    sealed class MainNavigation {
        data class NoInternet(@StringRes val errorId: Int) : MainNavigation()
        object HideBooksListLoading : MainNavigation()
        object ShowBooksListLoading : MainNavigation()
    }
}