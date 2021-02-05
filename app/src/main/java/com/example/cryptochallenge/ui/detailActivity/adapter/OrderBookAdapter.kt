package com.example.cryptochallenge.ui.detailActivity.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cryptochallenge.data.model.Order
import com.example.cryptochallenge.databinding.ViewHolderOrderBinding

class OrderBookAdapter : ListAdapter<Order, OrderBookViewHolder>(OrderDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderBookViewHolder {
        return OrderBookViewHolder(parent)
    }

    override fun onBindViewHolder(holder: OrderBookViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class OrderBookViewHolder private constructor(
    private val binding: ViewHolderOrderBinding
) : RecyclerView.ViewHolder(
    binding.root
) {
    constructor(parent: ViewGroup) : this(
        ViewHolderOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    fun bind(order: Order) {
        binding.tvPrice.text = order.price
        binding.tvAmount.text = order.amount
    }
}

class OrderDiffCallback : DiffUtil.ItemCallback<Order>() {
    override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
        return oldItem == newItem
    }
}