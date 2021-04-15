package com.example.cryptochallenge.ui.detailFragment

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.cryptochallenge.R
import com.example.cryptochallenge.data.model.Order
import com.example.cryptochallenge.data.model.Ticker
import com.example.cryptochallenge.ui.components.horizontalGradient
import com.example.cryptochallenge.ui.mainFragment.CryptoLogo
import com.example.cryptochallenge.ui.mainFragment.ProgressBar
import com.example.cryptochallenge.ui.theme.Ask
import com.example.cryptochallenge.ui.theme.Bid
import com.example.cryptochallenge.ui.theme.Btc
import com.example.cryptochallenge.utils.DominantColors
import com.example.cryptochallenge.utils.PriceChange
import com.example.cryptochallenge.utils.createUrlByTicker
import java.util.*

@Composable
fun DetailContent(
    viewModel: DetailViewModel,
    dominantColors: DominantColors,
    upNavigation: () -> Unit
) {
    val pair by viewModel.pair.observeAsState()
    val ticker by viewModel.ticker.observeAsState()
    val orderBook by viewModel.orderBook.observeAsState()
    val isLoadingEvent by viewModel.isLoading.observeAsState()
    Surface {
        ConstraintLayout(Modifier.fillMaxSize()) {
            val (appBar, orderBookList, button) = createRefs()
            DetailAppBar(
                dominantColors,
                pair = pair, ticker = ticker ?: Ticker.defaultTicker().copy(last = "0"),
                Modifier.constrainAs(appBar) {
                    top.linkTo(parent.top)
                },
                upNavigation = upNavigation
            )
            orderBook?.let {
                Row(
                    Modifier
                        .padding(dimensionResource(id = R.dimen.padding_small))
                        .constrainAs(orderBookList) {
                            top.linkTo(appBar.bottom)
                            bottom.linkTo(button.top)
                            height = Dimension.fillToConstraints
                        }
                ) {
                    OrderList(
                        orders = it.bids,
                        color = Bid,
                        Modifier
                            .weight(1f)
                            .padding(end = dimensionResource(id = R.dimen.padding_small))
                    )
                    Spacer(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(1.dp)
                            .background(Color.Black)
                    )
                    OrderList(
                        orders = it.asks,
                        color = Ask,
                        Modifier
                            .weight(1f)
                            .padding(start = dimensionResource(id = R.dimen.padding_small))
                    )
                }
            }
            isLoadingEvent?.getIfNotHandled()?.let {
                if (it) ProgressBar()
            }
            val smallPadding = dimensionResource(id = R.dimen.padding_small)
            Button(
                onClick = { viewModel.onReloadPressed(orderBook!!.bids[0].book) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = smallPadding, end = smallPadding, bottom = smallPadding)
                    .constrainAs(button) {
                        bottom.linkTo(parent.bottom)
                    }
            ) {
                Text(
                    text = stringResource(id = R.string.reload).toUpperCase(Locale.ROOT)
                )
            }
        }
    }
}

@Composable
fun DetailAppBar(
    dominantColors: DominantColors,
    pair: Pair<String, String>?,
    ticker: Ticker,
    modifier: Modifier = Modifier,
    upNavigation: () -> Unit
) {
    if (pair == null) return
    val pairString = stringResource(R.string.pair, pair.first, pair.second).toUpperCase(Locale.ROOT)
    val topCornerSize = CornerSize(0.dp)
    val bottomCornerSize = CornerSize(20.dp)

    val imageUrl = createUrlByTicker(pair.first)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(Alignment.Top),
        shape = RoundedCornerShape(
            topStart = topCornerSize, topEnd = topCornerSize,
            bottomStart = bottomCornerSize, bottomEnd = bottomCornerSize
        )
    ) {
        Column(
            Modifier
                .wrapContentHeight(Alignment.Top)
                .background(
                    horizontalGradient(color = dominantColors.color)
                )
                .padding(dimensionResource(id = R.dimen.padding_small))
        ) {
            ConstraintLayout(
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = dimensionResource(id = R.dimen.padding_small))
            ) {
                val (icon, logo, pairTitle, spacer) = createRefs()
                IconButton(
                    onClick = upNavigation,
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            color = Color.Black.copy(alpha = 0.12f),
                            shape = CircleShape
                        )
                        .constrainAs(icon) {
                            start.linkTo(parent.start)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                        }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "",
                        tint = Color.White
                    )
                }
                createHorizontalChain(logo, spacer, pairTitle, chainStyle = ChainStyle.Packed)
                CryptoLogo(
                    imageUrl = imageUrl,
                    Modifier.constrainAs(logo) {}
                )
                Spacer(
                    modifier = Modifier
                        .width(dimensionResource(id = R.dimen.margin_small))
                        .constrainAs(spacer) {}
                )
                Text(
                    text = pairString,
                    color = dominantColors.onColor,
                    modifier = Modifier.constrainAs(pairTitle) {
                        linkTo(top = parent.top, bottom = parent.bottom)
                    }
                )
            }

            val textColor by animateColorAsState(
                targetValue = dominantColors.onColor,
                animationSpec = keyframes {
                    durationMillis = 500
                    val color = when (ticker.lastPriceChangedRegardingThePreviousOne) {
                        PriceChange.CURRENT_IS_HIGHER -> Bid
                        PriceChange.CURRENT_IS_LOWER -> Ask
                        PriceChange.NO_CHANGE -> dominantColors.onColor
                    }
                    color at 100
                    color at 300
                }
            )
            Row(Modifier.fillMaxWidth()) {
                Text(
                    text = ticker.last,
                    style = MaterialTheme.typography.h3,
                    color = textColor,
                    modifier = Modifier.alignByBaseline()
                )
                Text(
                    text = ticker.currency,
                    style = MaterialTheme.typography.h4,
                    color = textColor,
                    modifier = Modifier.alignByBaseline()
                )
            }
            val fontColor = dominantColors.onColor
            Column(Modifier.padding(dimensionResource(id = R.dimen.padding_small))) {
                TickerElements(
                    firstElement = stringResource(id = R.string.bid_value, ticker.bid),
                    secondElement = stringResource(id = R.string.ask_value, ticker.ask),
                    fontColor = fontColor
                )
                TickerElements(
                    firstElement = stringResource(id = R.string.low_value, ticker.low),
                    secondElement = stringResource(id = R.string.high_value, ticker.high),
                    fontColor = fontColor
                )
                TickerElements(
                    firstElement = stringResource(id = R.string.vol_value, ticker.volume),
                    secondElement = ticker.createdAt,
                    fontColor = fontColor
                )
            }
        }
    }
}

@Composable
private fun TickerElements(
    firstElement: String,
    secondElement: String,
    fontColor: Color
) {
    Row {
        Text(
            text = firstElement,
            Modifier.weight(1f),
            fontSize = 14.sp,
            color = fontColor
        )
        Text(
            text = secondElement,
            Modifier.weight(1f),
            fontSize = 14.sp,
            color = fontColor
        )
    }
}

@Composable
private fun OrderList(orders: List<Order>, color: Color, modifier: Modifier = Modifier) {
    LazyColumn(modifier) {
        item {
            OrderHeader(order = orders[0], color = color)
        }
        items(items = orders) {
            OrderItem(order = it)
        }
    }
}

@Composable
fun OrderHeader(order: Order, color: Color) {
    val pair = order.book.split("_")
    Row(Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(id = R.string.price_order, pair[0]),
            Modifier.weight(1f), style = MaterialTheme.typography.body2,
            color = color
        )
        Text(
            text = stringResource(id = R.string.amount_order, pair[1]),
            Modifier.weight(1f),
            style = MaterialTheme.typography.body2, textAlign = TextAlign.End
        )
    }
}

@Composable
fun OrderItem(order: Order) {
    Row(Modifier.fillMaxWidth()) {
        Text(text = order.price, style = MaterialTheme.typography.caption)
        Text(
            text = order.amount, Modifier.weight(1f),
            style = MaterialTheme.typography.caption, textAlign = TextAlign.End
        )
    }
}

@Preview
@Composable
fun PreviewDetailAppBar() {
    DetailAppBar(
        DominantColors(Btc, Color.White),
        pair = Pair("btc", "mxn"), ticker = Ticker.defaultTicker().copy(currency = "MXN"), upNavigation = {}
    )
}

@Preview
@Composable
fun PreviewOrderItem() {
    val order = Order("btc_mxn", "1234567", "1.00000300")
    OrderItem(order = order)
}