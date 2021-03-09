package com.example.cryptochallenge.ui.detailFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.cryptochallenge.ui.components.SetStatusBarGradient
import com.example.cryptochallenge.ui.theme.*
import com.example.cryptochallenge.utils.DominantColors
import com.example.cryptochallenge.utils.EventObserver
import com.example.cryptochallenge.utils.showSnackBar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailFragment : Fragment() {
    private val viewModel: DetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val dominantColors: DominantColors = navArgs<DetailFragmentArgs>().value.intentDominantColors
                SetStatusBarGradient(
                    window = activity?.window,
                    startColor = dominantColors.color.copy(0.8f).toArgb(),
                    endColor = dominantColors.color.toArgb()
                )
                CryptoTheme {
                    DetailContent(viewModel = viewModel, dominantColors) {
                        findNavController().navigateUp()
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRequest()
        setObservers()
    }

    private fun initRequest() {
        val book: String = navArgs<DetailFragmentArgs>().value.intentBook
        viewModel.onInitialRequest(book)
    }

    private fun setObservers() {
        viewModel.error.observe(
            viewLifecycleOwner,
            EventObserver {
                view?.showSnackBar(it)
            }
        )
    }
}