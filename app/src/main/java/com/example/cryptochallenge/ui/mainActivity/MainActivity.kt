package com.example.cryptochallenge.ui.mainActivity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cryptochallenge.CryptoApp
import com.example.cryptochallenge.databinding.ActivityMainBinding
import com.example.cryptochallenge.ui.detailActivity.DetailActivity
import com.example.cryptochallenge.ui.mainActivity.adapter.MainAdapter
import com.example.cryptochallenge.utils.EventObserver
import com.example.cryptochallenge.utils.ViewModelFactory
import com.example.cryptochallenge.utils.showToast
import io.reactivex.disposables.CompositeDisposable

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels {
        ViewModelFactory(MainViewModel::class) {
            val repository = (application as CryptoApp).getBooksRepository()
            val network = (application as CryptoApp).getNetworkHelper()
            return@ViewModelFactory MainViewModel(repository, network, CompositeDisposable())
        }
    }

    private val adapter: MainAdapter by lazy {
        MainAdapter {
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra(MainViewModel.INTENT_BOOK, it.book)
            startActivity(intent)
        }
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initRecycler()

        setObservers()
        setListeners()

        initRequest()
    }

    private fun initRequest() {
        viewModel.onInitialRequest()
    }

    private fun setObservers() {
        viewModel.events.observe(
            this,
            EventObserver {
                when (it) {
                    is MainViewModel.MainNavigation.Error -> {
                        showToast(it.errorId)
                    }
                    MainViewModel.MainNavigation.HideBooksListLoading -> {
                        binding.srlCoins.isRefreshing = false
                    }
                    MainViewModel.MainNavigation.ShowBooksListLoading -> {
                        if (!binding.srlCoins.isRefreshing) binding.srlCoins.isRefreshing = true
                        binding.tvNoInternet.visibility = View.GONE
                    }
                    is MainViewModel.MainNavigation.BooksList -> {
                        adapter.submitList(it.books)
                    }
                    MainViewModel.MainNavigation.NoDataFound -> {
                        binding.tvNoInternet.visibility = View.VISIBLE
                    }
                }
            }
        )
    }

    private fun setListeners() {
        binding.srlCoins.setOnRefreshListener {
            viewModel.onReload()
        }
    }

    private fun initRecycler() {
        binding.rvBooks.layoutManager = LinearLayoutManager(this)
        binding.rvBooks.adapter = adapter
    }
}