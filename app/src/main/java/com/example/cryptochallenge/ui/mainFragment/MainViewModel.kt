package com.example.cryptochallenge.ui.mainFragment

import androidx.annotation.StringRes
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cryptochallenge.R
import com.example.cryptochallenge.data.model.Book
import com.example.cryptochallenge.data.repository.BooksRepository
import com.example.cryptochallenge.utils.Event
import com.example.cryptochallenge.utils.connectivity.NetworkHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

class MainViewModel @ViewModelInject constructor(
    private val booksRepository: BooksRepository,
    private val networkHelper: NetworkHelper,
    private val compositeDisposable: CompositeDisposable
) : ViewModel() {

    companion object {
        const val INTENT_BOOK = "intent_book"
    }

    private val _events = MutableLiveData<Event<MainNavigation>>()
    val events: LiveData<Event<MainNavigation>> = _events

    private val _data = MutableLiveData<RequestResult>()
    val data: LiveData<RequestResult> = _data

    /*
    * onInitialRequest() will be called every time onViewCreated() (from mainFragment).
    * `if (events.value != null)` is used to avoid calling it when config changes occur
    * It's necessary to check manually for the internet connection status as explained
    * in NetworkHelperImpl
    */
    fun onInitialRequest() {
        if (events.value != null) return
        networkHelper.observable()
            .subscribe(
                { internetChange(it) },
                { it.printStackTrace() }
            ).let { compositeDisposable.add(it) }
        if (!networkHelper.isNetworkConnected()) {
            _events.value = Event(MainNavigation.ShowBooksListLoading)
            requestLocalBooks()
        }
    }

    private fun internetChange(isConnected: Boolean) {
        if (isConnected) {
            _events.value = Event(MainNavigation.ShowBooksListLoading)
            requestRemoteBooks()
        } else {
            _events.value = Event(MainNavigation.Error(R.string.error_no_internet))
            _events.value = Event(MainNavigation.HideBooksListLoading)
        }
    }

    private fun requestRemoteBooks() {
        booksRepository.requestBooks()
            .flatMap { books ->
                booksRepository.saveList(books).map { books }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    _data.value = RequestResult.BooksList(it)
                    _events.value = Event(MainNavigation.HideBooksListLoading)
                },
                {
                    it.printStackTrace()
                    _events.value = Event(MainNavigation.Error(R.string.error_on_request_books))
                    requestLocalBooks()
                }
            )
            .let { compositeDisposable.add(it) }
    }

    private fun requestLocalBooks() {
        booksRepository.getLocalBooks()
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally {
                _events.value = Event(MainNavigation.HideBooksListLoading)
            }
            .subscribe(
                {
                    if (it.isEmpty()) _data.value = RequestResult.NoDataFound
                    else _data.value = RequestResult.BooksList(it)
                },
                {
                    it.printStackTrace()
                    _data.value = RequestResult.NoDataFound
                }
            )
            .let { compositeDisposable.add(it) }
    }

    fun onReload() {
        internetChange(networkHelper.isNetworkConnected())
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

    sealed class RequestResult {
        data class BooksList(val books: List<Book>) : RequestResult()
        object NoDataFound : RequestResult()
    }

    sealed class MainNavigation {
        data class Error(@StringRes val errorId: Int) : MainNavigation()
        object HideBooksListLoading : MainNavigation()
        object ShowBooksListLoading : MainNavigation()
    }
}