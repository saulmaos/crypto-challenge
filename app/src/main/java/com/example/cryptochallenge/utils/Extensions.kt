package com.example.cryptochallenge.utils

import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.FragmentActivity
import com.example.cryptochallenge.R
import com.example.cryptochallenge.data.local.entity.BookEntity
import com.example.cryptochallenge.data.local.entity.OrderEntity
import com.example.cryptochallenge.data.local.entity.TickerEntity
import com.example.cryptochallenge.data.model.Book
import com.example.cryptochallenge.data.model.Order
import com.example.cryptochallenge.data.model.OrderBook
import com.example.cryptochallenge.data.model.Ticker
import com.example.cryptochallenge.data.remote.response.PayloadAvailableBookResponse
import com.example.cryptochallenge.data.remote.response.PayloadOrderBookResponse
import com.example.cryptochallenge.data.remote.response.PayloadTickerResponse
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso

fun ImageView.loadUrl(url: String) {
    Picasso
        .with(this.context)
        .load(url)
        .placeholder(R.drawable.loading_animation)
        .error(R.drawable.error)
        .into(this)
}

fun FragmentActivity.showToast(@StringRes msg: Int) {
    Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
}

fun View.showSnackBar(@StringRes msg: Int) {
    Snackbar.make(this, msg, Snackbar.LENGTH_SHORT).show()
}

fun List<BookEntity>.toBookList(): List<Book> = map {
    Book(
        it.book, "", "", "",
        "", "", "", CryptoIcons.createUrl(it.book)
    )
}

fun List<Book>.toBookEntityList(): List<BookEntity> = map {
    BookEntity(book = it.book)
}

fun Ticker.toTickerEntity(): TickerEntity =
    TickerEntity(book, volume, high, last, low, vwap, ask, bid, createdAt)

fun TickerEntity.toTicker(): Ticker =
    Ticker(book, volume, high, last, low, vwap, ask, bid, createdAt)

fun List<Order>.toOrderEntityList(orderType: OrderType): List<OrderEntity> = map {
    OrderEntity(book = it.book, price = it.price, amount = it.amount, orderType = orderType.type)
}

fun List<OrderEntity>.toOrderList(): List<Order> = map {
    Order(it.book, it.price, it.price)
}

fun PayloadAvailableBookResponse.toBook() =
    Book(
        book,
        minimumAmount,
        maximumAmount,
        minimumPrice,
        maximumPrice,
        minimumValue,
        maximumValue,
        CryptoIcons.createUrl(book)
    )

fun PayloadTickerResponse.toTicker() =
    Ticker(
        book,
        volume,
        high,
        last,
        low,
        vwap,
        ask,
        bid,
        createdAt
    )

fun PayloadOrderBookResponse.toOrderBook(): OrderBook {
    val bids: List<Order> = bids.map {
        Order(it.book, it.price, it.amount)
    }
    val asks: List<Order> = asks.map {
        Order(it.book, it.price, it.amount)
    }
    return OrderBook(asks, bids)
}