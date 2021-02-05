package com.example.cryptochallenge.ui.detailFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cryptochallenge.CryptoApp
import com.example.cryptochallenge.R
import com.example.cryptochallenge.databinding.FragmentDetailBinding
import com.example.cryptochallenge.ui.detailFragment.adapter.OrderBookAdapter
import com.example.cryptochallenge.utils.EventObserver
import com.example.cryptochallenge.utils.ViewModelFactory
import com.example.cryptochallenge.utils.showSnackBar
import io.reactivex.disposables.CompositeDisposable

class DetailFragment : Fragment() {

    private lateinit var binding: FragmentDetailBinding

    private val viewModel: DetailViewModel by viewModels {
        ViewModelFactory(DetailViewModel::class) {
            val repository = (requireActivity().application as CryptoApp).getCoinDetailsRepository()
            val networkHelper = (requireActivity().application as CryptoApp).getNetworkHelper()
            DetailViewModel(repository, networkHelper, CompositeDisposable())
        }
    }

    private val bidsAdapter: OrderBookAdapter by lazy {
        OrderBookAdapter()
    }
    private val asksAdapter: OrderBookAdapter by lazy {
        OrderBookAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentDetailBinding.inflate(LayoutInflater.from(context), container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecycler()
        initRequest()
        setObservers()
        setListeners()
    }

    private fun initRequest() {
        val book: String = navArgs<DetailFragmentArgs>().value.intentBook
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
            val book: String = navArgs<DetailFragmentArgs>().value.intentBook
            viewModel.onReloadPressed(book)
        }
    }

    private fun setObservers() {
        viewModel.events.observe(
            viewLifecycleOwner,
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
            viewLifecycleOwner,
            {
                val ticker = it.first
                val currency = it.second
                (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.pair, ticker, currency)
                binding.tvPriceBid.text = getString(R.string.price_order, currency)
                binding.tvPriceAsk.text = getString(R.string.price_order, currency)
                binding.tvAmountBid.text = getString(R.string.amount_order, ticker)
                binding.tvAmountAsk.text = getString(R.string.amount_order, ticker)
            }
        )
    }
}