package com.example.cryptochallenge.ui.mainFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.cryptochallenge.ui.components.SetStatusBarGradient
import com.example.cryptochallenge.ui.theme.CryptoTheme
import com.example.cryptochallenge.utils.EventObserver
import com.example.cryptochallenge.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment() {

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                CryptoTheme {
                    SetStatusBarGradient(activity?.window)
                    MainContent(
                        mainViewModel = viewModel,
                        onItemClick = { book, dominantColors ->
                            findNavController().navigate(
                                MainFragmentDirections.actionMainFragmentToDetailFragment(
                                    book.book, dominantColors
                                )
                            )
                        }
                    )
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setObservers()
        initRequest()
    }

    private fun initRequest() {
        viewModel.onInitialRequest()
    }

    private fun setObservers() {
        viewModel.error.observe(
            viewLifecycleOwner,
            EventObserver {
                requireActivity().showToast(it)
            }
        )
    }
}