package com.example.cryptochallenge.ui.mainFragment

import androidx.collection.LruCache
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.cryptochallenge.R
import com.example.cryptochallenge.data.model.Book
import com.example.cryptochallenge.ui.components.horizontalGradient
import com.example.cryptochallenge.ui.theme.CryptoTheme
import com.example.cryptochallenge.ui.theme.onAppBarGradient
import com.example.cryptochallenge.utils.DominantColors
import com.example.cryptochallenge.utils.rememberDominantColorState
import dev.chrisbanes.accompanist.coil.CoilImage

@Composable
fun MainContent(mainViewModel: MainViewModel, onItemClick: (book: Book, dominantColors: DominantColors) -> Unit) {
    Column(Modifier.fillMaxSize()) {
        AppBar()
        Divider()
        BodyContent(mainViewModel = mainViewModel, onItemClick = onItemClick)
    }
}

@Composable
fun AppBar() {
    Row(
        Modifier
            .fillMaxWidth()
            .height(dimensionResource(id = R.dimen.appbar_height))
    ) {
        Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.margin_normal)))
        Text(
            text = stringResource(id = R.string.app_name),
            modifier = Modifier.align(Alignment.CenterVertically),
            style = MaterialTheme.typography.h6,
            color = MaterialTheme.colors.onAppBarGradient
        )
    }
}

@Composable
private fun BodyContent(
    mainViewModel: MainViewModel,
    onItemClick: (book: Book, dominantColors: DominantColors) -> Unit
) {
    Surface {
        Coins(mainViewModel = mainViewModel, onItemClick = onItemClick)
        LoadingCompose(mainViewModel = mainViewModel)
    }
}

@Composable
private fun Coins(mainViewModel: MainViewModel, onItemClick: (book: Book, dominantColors: DominantColors) -> Unit) {
    val coins by mainViewModel.data.observeAsState()
    when (coins) {
        is MainViewModel.RequestResult.BooksList -> {
            CryptoList(
                coins = (coins as MainViewModel.RequestResult.BooksList).books,
                onItemClick = onItemClick
            )
        }
        MainViewModel.RequestResult.NoDataFound -> {
            NoData()
        }
    }
}

@Composable
private fun LoadingCompose(mainViewModel: MainViewModel) {
    val isLoadingEvent by mainViewModel.isLoading.observeAsState()
    isLoadingEvent?.getIfNotHandled()?.let {
        if (it) ProgressBar()
    }
}

@Composable
private fun NoData() {
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.no_internet),
            contentDescription = "error",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        )
        Text(
            text = stringResource(id = R.string.error_no_internet_long),
            style = MaterialTheme.typography.h5,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_small)))
        Text(
            text = stringResource(id = R.string.error_no_internet_keep_calm),
            style = MaterialTheme.typography.subtitle2,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
private fun CryptoList(coins: List<Book>, onItemClick: (book: Book, dominantColors: DominantColors) -> Unit) {
    val cache = remember { LruCache<String, DominantColors>(10) }
    LazyColumn {
        items(items = coins) {
            CryptoItem(coin = it, cache = cache, onItemClick = onItemClick)
        }
    }
}

@Composable
private fun CryptoItem(
    coin: Book,
    cache: LruCache<String, DominantColors>,
    onItemClick: (book: Book, dominantColors: DominantColors) -> Unit
) {
    val dominantColorState = rememberDominantColorState(
        defaultColor = Color.LightGray,
        defaultOnColor = Color.DarkGray,
        cache = cache
    )
    Card(
        Modifier
            .fillMaxWidth()
            .height(dimensionResource(id = R.dimen.height_card_crypto_item))
            .padding(dimensionResource(id = R.dimen.padding_small))
            .clickable(
                onClick = {
                    onItemClick(
                        coin,
                        DominantColors(dominantColorState.color, dominantColorState.onColor)
                    )
                }
            )
    ) {
        LaunchedEffect(key1 = coin.imageUrl) {
            dominantColorState.updateColorsFromImageUrl(coin.imageUrl)
        }

        ConstraintLayout(
            modifier = Modifier
                .background(
                    horizontalGradient(color = dominantColorState.color)
                )
        ) {
            val (image, row) = createRefs()
            var isVisible by remember(coin.book) { mutableStateOf(false) }
            val alpha by animateFloatAsState(
                targetValue = if (isVisible) 0.4f else 0f,
                animationSpec = tween(durationMillis = 1000)
            )
            CoilImage(
                data = coin.imageUrl,
                contentDescription = "",
                Modifier
                    .size(dimensionResource(id = R.dimen.crypto_logo_size_big))
                    .alpha(alpha = alpha)
                    .constrainAs(image) {
                        end.linkTo(parent.end)
                        start.linkTo(parent.end)
                    },
                contentScale = ContentScale.Crop,
                onRequestCompleted = { isVisible = true }
            )
            Row(
                Modifier
                    .padding(dimensionResource(id = R.dimen.padding_normal))
                    .constrainAs(row) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }
            ) {
                CryptoLogo(imageUrl = coin.imageUrl)
                Text(
                    text = coin.bookPretty,
                    color = dominantColorState.onColor,
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(start = dimensionResource(id = R.dimen.padding_small))
                )
            }
        }
    }
}

@Composable
fun CryptoLogo(imageUrl: String, modifier: Modifier = Modifier) {
    CoilImage(
        data = imageUrl,
        contentDescription = "Crypto Logo",
        modifier
            .size(dimensionResource(id = R.dimen.crypto_logo_size))
            .border(2.dp, Color.White, CircleShape),
        fadeIn = true,
        error = {
            Image(
                painter = painterResource(id = R.drawable.error),
                contentDescription = ""
            )
        },
        loading = {
            val infiniteTransition = rememberInfiniteTransition()
            val rotationDegree by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 365f,
                animationSpec = infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = 500
                    },
                    repeatMode = RepeatMode.Restart
                )
            )
            Image(
                painter = painterResource(id = R.drawable.loading_img),
                contentDescription = "",
                Modifier.rotate(rotationDegree)
            )
        }
    )
}

@Composable
fun ProgressBar() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(Modifier.size(50.dp))
    }
}

@Preview
@Composable
fun PreviewCryptoItem() {
    val book = Book(
        "btc_mxn", "1", "1", "1", "1",
        "1", "1", "1"
    )
    val cache = LruCache<String, DominantColors>(10)
    CryptoTheme {
        CryptoItem(coin = book, cache, onItemClick = { _, _ -> })
    }
}

@Preview
@Composable
fun PreviewCryptoList() {
    val book = Book(
        "btc_mxn", "1", "1", "1", "1",
        "1", "1", "1"
    )
    CryptoTheme {
        CryptoList(
            coins = listOf(book, book.copy(book = "eth_mxn", bookPretty = "eth/mxn")),
            onItemClick = { _, _ -> }
        )
    }
}

@Preview
@Composable
fun PreviewNoData() {
    CryptoTheme {
        Scaffold {
            NoData()
        }
    }
}