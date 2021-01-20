package com.example.cryptochallenge.ui.detailActivity

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.cryptochallenge.CryptoApp
import com.example.cryptochallenge.R
import com.example.cryptochallenge.data.model.coin
import com.example.cryptochallenge.data.model.currency
import com.example.cryptochallenge.ui.mainActivity.MainViewModel
import com.example.cryptochallenge.utils.EventObserver
import com.example.cryptochallenge.utils.ViewModelFactory
import com.example.cryptochallenge.utils.showToast
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    private val viewModel: DetailViewModel by viewModels {
        ViewModelFactory(DetailViewModel::class) {
            val repository = (application as CryptoApp).coinDetailsRepository
            DetailViewModel(repository)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        setObservers()
        setListeners()
    }

    private fun setListeners() {
        btnReload.setOnClickListener {
            val book: String? = intent.getStringExtra(MainViewModel.INTENT_BOOK)
            viewModel.onReloadPressed(book)
        }
    }

    private fun setObservers() {
        viewModel.ticker.observe(this, EventObserver {
            when(it) {
                is DetailViewModel.TickerResponse.TickerResult -> {
                    tvLastPriceVal.text = it.ticker.last
                    tvCurrency.text = it.ticker.currency
                }
                is DetailViewModel.TickerResponse.Error -> showToast(it.errorId)
                is DetailViewModel.TickerResponse.TickerDefaultResult -> tvLastPriceVal.text = it.default
            }
        })
        viewModel.lastOrder.observe(this, EventObserver {
            when(it) {
                is DetailViewModel.OrderResponse.OrderResult -> {
                    tvPriceVal.text = getString(R.string.coin_and_currency_format, it.order.price, it.order.currency())
                    tvAmountVal.text = getString(R.string.coin_and_currency_format, it.order.amount, it.order.coin())
                }
                is DetailViewModel.OrderResponse.Error -> showToast(it.errorId)
            }

        })
        viewModel.events.observe(this, EventObserver {
            when(it) {
                is DetailViewModel.DetailNavigation.NoInternet -> showToast(it.errorId)
                DetailViewModel.DetailNavigation.HideLoading -> {
                    progressBar.visibility = View.GONE
                }
                DetailViewModel.DetailNavigation.ShowLoading -> {
                    progressBar.visibility = View.VISIBLE
                }
            }
        })
        viewModel.pair.observe(this, {
            title = it
        })
        val connectivityLiveData = (application as CryptoApp).connectivityLiveData
        connectivityLiveData.observe(this, {
            val book: String? = intent.getStringExtra(MainViewModel.INTENT_BOOK)
            viewModel.onInternetChange(it, book)
        })
    }
}