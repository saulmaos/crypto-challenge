package com.example.cryptochallenge.utils

import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.FragmentActivity
import com.example.cryptochallenge.R
import com.example.cryptochallenge.data.local.entity.BookEntity
import com.example.cryptochallenge.data.model.Book
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
    Toast.makeText(this.applicationContext, msg, Toast.LENGTH_SHORT).show()
}

fun List<BookEntity>.toBookList(): List<Book> = map {
    Book(it.book, "", "", "",
        "", "", "", CryptoIcons.createUrl(it.book))
}

fun List<Book>.toBookEntityList(): List<BookEntity> = map {
    BookEntity(book = it.book)
}