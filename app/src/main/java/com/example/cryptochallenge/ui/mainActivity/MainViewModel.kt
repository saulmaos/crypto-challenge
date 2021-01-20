package com.example.cryptochallenge.ui.mainActivity

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.cryptochallenge.R
import com.example.cryptochallenge.data.local.entity.BookEntity
import com.example.cryptochallenge.data.model.Book
import com.example.cryptochallenge.data.repository.BooksRepository
import com.example.cryptochallenge.data.repository.NetworkResponse
import com.example.cryptochallenge.utils.Event
import com.example.cryptochallenge.utils.toBookEntityList
import io.reactivex.disposables.CompositeDisposable

class MainViewModel(
    private val booksRepository: BooksRepository,
    private val compositeDisposable: CompositeDisposable
) : ViewModel() {

    companion object {
        const val INTENT_BOOK = "intent_book"
    }

    init {
        requestLocalBooks()
    }

    private val _events = MutableLiveData<Event<MainNavigation>>()
    val events: LiveData<Event<MainNavigation>> = _events

    val books: LiveData<Event<BooksResponse>> =
        Transformations.map(booksRepository.availableBooks) {
            when (it) {
                is NetworkResponse.Success<List<Book>> -> {
                    Event(BooksResponse.BooksList(it.data))
                }
                is NetworkResponse.Error -> {
                    Event(BooksResponse.Error(it.errorId))
                }
            }
        }

    fun onInternetChange(isConnected: Boolean) {
        if (isConnected) {
            _events.value = Event(MainNavigation.ShowBooksListLoading)
            requestRemoteBooks()
        } else {
            _events.value = Event(MainNavigation.NoInternet(R.string.error_no_internet))
        }
    }

    private fun requestRemoteBooks() {
        booksRepository.requestBooks()
            .map {
                val list: List<BookEntity> = it.toBookEntityList()
                booksRepository.saveList(list)
                it
            }
            .subscribe(
                {
                    _events.value = Event(MainNavigation.BooksList(it))
                    _events.value = Event(MainNavigation.HideBooksListLoading)
                },
                {
                    it.printStackTrace()
                    _events.value = Event(MainNavigation.NoInternet(R.string.error_on_request_books))
                    _events.value = Event(MainNavigation.HideBooksListLoading)
                }
            )
            .let {
                compositeDisposable.add(it)
            }
    }

    private fun requestLocalBooks() {
        booksRepository.getLocalBooks()
            .subscribe(
                {
                    _events.value = Event(MainNavigation.BooksList(it))
                },
                { it.printStackTrace() }
            )
            .let { compositeDisposable.add(it) }
    }

    fun onReloadPressed() {
        onInternetChange(true)
    }

    override fun onCleared() {
        super.onCleared()
    }

    sealed class BooksResponse {
        data class BooksList(val books: List<Book>) : BooksResponse()
        data class Error(@StringRes val errorId: Int) : BooksResponse()
    }

    sealed class MainNavigation {
        data class BooksList(val books: List<Book>) : MainNavigation()
        data class NoInternet(@StringRes val errorId: Int) : MainNavigation()
        object HideBooksListLoading : MainNavigation()
        object ShowBooksListLoading : MainNavigation()
    }
}