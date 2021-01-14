package com.example.cryptochallenge.ui.mainActivity.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cryptochallenge.R
import com.example.cryptochallenge.data.model.Book
import com.example.cryptochallenge.utils.loadUrl
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.view_holder_book.view.*

class MainAdapter(
    private val listener: (book: Book) -> Unit
) : ListAdapter<Book, MainViewHolder>(BookDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(parent, listener)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class MainViewHolder(
    parent: ViewGroup,
    private val listener: (book: Book) -> Unit
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.view_holder_book, parent, false)
) {
    fun bind(book: Book) {
        itemView.tvBook.text = book.bookPretty
        itemView.ivLogo.loadUrl(book.imageUrl)
        itemView.setOnClickListener { listener(book) }
    }
}

class BookDiffCallback : DiffUtil.ItemCallback<Book>() {
    override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {
        return oldItem == newItem
    }
}