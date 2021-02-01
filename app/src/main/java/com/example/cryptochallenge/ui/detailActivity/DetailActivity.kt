package com.example.cryptochallenge.ui.detailActivity

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cryptochallenge.CryptoApp
import com.example.cryptochallenge.R
import com.example.cryptochallenge.databinding.ActivityDetailBinding
import com.example.cryptochallenge.ui.detailActivity.adapter.OrderBookAdapter
import com.example.cryptochallenge.ui.mainActivity.MainViewModel
import com.example.cryptochallenge.utils.EventObserver
import com.example.cryptochallenge.utils.ViewModelFactory
import com.example.cryptochallenge.utils.showSnackBar
import io.reactivex.disposables.CompositeDisposable

class DetailActivity : AppCompatActivity() {

    private val viewModel: DetailViewModel by viewModels {
        ViewModelFactory(DetailViewModel::class) {
            val repository = (application as CryptoApp).getCoinDetailsRepository()
            val networkHelper = (application as CryptoApp).getNetworkHelper()
            DetailViewModel(repository, networkHelper, CompositeDisposable())
        }
    }

    private val bidsAdapter: OrderBookAdapter by lazy {
        OrderBookAdapter()
    }
    private val asksAdapter: OrderBookAdapter by lazy {
        OrderBookAdapter()
    }
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initRecycler()
        initRequest()
        setObservers()
        setListeners()
    }

    private fun initRequest() {
        val book: String? = intent.getStringExtra(MainViewModel.INTENT_BOOK)
        viewModel.onInitialRequest(book)
    }

    private fun initRecycler() {
        binding.rvBids.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = bidsAdapter
        }
        binding.rvAsks.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = asksAdapter
        }
    }

    private fun setListeners() {
        binding.btnReload.setOnClickListener {
            val book: String? = intent.getStringExtra(MainViewModel.INTENT_BOOK)
            viewModel.onReloadPressed(book)
        }
    }

    private fun setObservers() {
        viewModel.events.observe(
            this,
            EventObserver {
                when (it) {
                    is DetailViewModel.DetailNavigation.Error -> {
                        binding.clDetail.showSnackBar(it.errorId)
                    }
                    DetailViewModel.DetailNavigation.HideLoading -> {
                        binding.progressBar.visibility = View.GONE
                    }
                    DetailViewModel.DetailNavigation.ShowLoading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is DetailViewModel.DetailNavigation.TickerResult -> {
                        binding.tvLastPriceVal.text = it.ticker.last
                        binding.tvCurrency.text = it.ticker.currency
                        binding.tvAskVal.text = it.ticker.ask
                        binding.tvBidVal.text = it.ticker.bid
                        binding.tvLastUpdate.text = getString(R.string.last_update, it.ticker.createdAt)
                        binding.tvHighVal.text = it.ticker.high
                        binding.tvLowVal.text = it.ticker.low
                        binding.tvVolumeVal.text = it.ticker.volume
                    }
                    is DetailViewModel.DetailNavigation.OrderBookResult -> {
                        bidsAdapter.submitList(it.orderBook.bids)
                        asksAdapter.submitList(it.orderBook.asks)
                    }
                }
            }
        )
        viewModel.pair.observe(
            this,
            {
                val ticker = it.first
                val currency = it.second
                title = getString(R.string.pair, ticker, currency)
                binding.tvPriceBid.text = getString(R.string.price_order, currency)
                binding.tvPriceAsk.text = getString(R.string.price_order, currency)
                binding.tvAmountBid.text = getString(R.string.amount_order, ticker)
                binding.tvAmountAsk.text = getString(R.string.amount_order, ticker)
            }
        )
    }
}