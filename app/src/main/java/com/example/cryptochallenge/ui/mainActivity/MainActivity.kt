package com.example.cryptochallenge.ui.mainActivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cryptochallenge.CryptoApp
import com.example.cryptochallenge.R
import com.example.cryptochallenge.ui.detailActivity.DetailActivity
import com.example.cryptochallenge.ui.detailActivity.DetailViewModel
import com.example.cryptochallenge.ui.mainActivity.adapter.MainAdapter
import com.example.cryptochallenge.utils.EventObserver
import com.example.cryptochallenge.utils.ViewModelFactory
import com.example.cryptochallenge.utils.showToast
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

//    private val viewModel: MainViewModel by viewModels {
//        ViewModelFactory(MainViewModel::class) {
//            val repository = (application as CryptoApp).booksRepository
//            return@ViewModelFactory MainViewModel(repository, CompositeDisposable())
//        }
//    }

    private lateinit var viewModel: MainViewModel

    private lateinit var rvBooks: RecyclerView
    private val adapter: MainAdapter by lazy {
        MainAdapter{
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra(MainViewModel.INTENT_BOOK, it.book)
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val repository = (application as CryptoApp).booksRepository
//        val factory = ViewModelFactory(MainViewModel::class) {
//            MainViewModel(repository, CompositeDisposable())
//        }
        val factory = object: ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return MainViewModel(repository, CompositeDisposable()) as T
            }

        }
        viewModel = ViewModelProvider(this, factory).get(MainViewModel::class.java)

        initRecycler()

        setObservers()
        setListener()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun setObservers() {
        viewModel.books.observe(this, EventObserver{
            when(it) {
                is MainViewModel.BooksResponse.BooksList -> {
                    rvBooks.visibility = View.VISIBLE
                    btnReload.visibility = View.GONE
//                    adapter.submitList(it.books)
                }
                is MainViewModel.BooksResponse.Error -> {
                    rvBooks.visibility = View.GONE
                    btnReload.visibility = View.VISIBLE
                    showToast(it.errorId)
                }
            }

        })
        viewModel.events.observe(this, EventObserver{
            when(it) {
                is MainViewModel.MainNavigation.NoInternet -> {
                    btnReload.visibility = View.GONE
                    showToast(it.errorId)
                }
                MainViewModel.MainNavigation.HideBooksListLoading -> {
                    progressBar.visibility = View.GONE
                    tvNoInternet.visibility = View.GONE
                }
                MainViewModel.MainNavigation.ShowBooksListLoading -> {
                    progressBar.visibility = View.VISIBLE
                    btnReload.visibility = View.GONE
                    tvNoInternet.visibility = View.GONE
                }
                is MainViewModel.MainNavigation.BooksList -> {
                    rvBooks.visibility = View.VISIBLE
                    btnReload.visibility = View.GONE
                    adapter.submitList(it.books)
                }
            }
        })
        val connectivityLiveData = (application as CryptoApp).connectivityLiveData
        connectivityLiveData.observe(this, {
            viewModel.onInternetChange(it)
        })
    }

    private fun setListener() {
        btnReload.setOnClickListener {
            viewModel.onReloadPressed()
        }
    }

    private fun initRecycler() {
        rvBooks = findViewById(R.id.rvBooks)
        rvBooks.layoutManager = LinearLayoutManager(this)
        rvBooks.adapter = adapter
    }
}