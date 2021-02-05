package com.example.cryptochallenge.ui.mainFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cryptochallenge.CryptoApp
import com.example.cryptochallenge.databinding.FragmentMainBinding
import com.example.cryptochallenge.ui.mainFragment.adapter.MainAdapter
import com.example.cryptochallenge.utils.EventObserver
import com.example.cryptochallenge.utils.ViewModelFactory
import com.example.cryptochallenge.utils.showToast
import io.reactivex.disposables.CompositeDisposable

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding

    private val viewModel: MainViewModel by viewModels {
        ViewModelFactory(MainViewModel::class) {
            val repository = (requireActivity().application as CryptoApp).getBooksRepository()
            val network = (requireActivity().application as CryptoApp).getNetworkHelper()
            return@ViewModelFactory MainViewModel(repository, network, CompositeDisposable())
        }
    }

    private val booksAdapter: MainAdapter by lazy {
        MainAdapter {
            findNavController().navigate(MainFragmentDirections.actionMainFragmentToDetailFragment(it.book))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(LayoutInflater.from(context), container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
            viewLifecycleOwner,
            EventObserver {
                when (it) {
                    is MainViewModel.MainNavigation.Error -> {
                        requireActivity().showToast(it.errorId)
                    }
                    MainViewModel.MainNavigation.HideBooksListLoading -> {
                        binding.srlCoins.isRefreshing = false
                    }
                    MainViewModel.MainNavigation.ShowBooksListLoading -> {
                        if (!binding.srlCoins.isRefreshing) binding.srlCoins.isRefreshing = true
                        binding.tvNoInternet.visibility = View.GONE
                    }
                    is MainViewModel.MainNavigation.BooksList -> {
                        booksAdapter.submitList(it.books)
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
        binding.rvBooks.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = booksAdapter
        }
    }
}