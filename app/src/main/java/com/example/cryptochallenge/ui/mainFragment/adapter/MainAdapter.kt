package com.example.cryptochallenge.ui.mainFragment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cryptochallenge.data.model.Book
import com.example.cryptochallenge.databinding.ViewHolderBookBinding
import com.example.cryptochallenge.utils.loadUrl

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

class MainViewHolder private constructor(
    private val listener: (book: Book) -> Unit,
    private val binding: ViewHolderBookBinding
) : RecyclerView.ViewHolder(
    binding.root
) {
    constructor(parent: ViewGroup, listener: (book: Book) -> Unit) : this(
        listener, ViewHolderBookBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    fun bind(book: Book) {
        binding.tvBook.text = book.bookPretty
        binding.ivLogo.loadUrl(book.imageUrl)
        binding.root.setOnClickListener { listener(book) }
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