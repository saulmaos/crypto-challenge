package com.example.cryptochallenge.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "order")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0,

    @ColumnInfo(name = "book")
    var book: String,

    @ColumnInfo(name = "price")
    var price: String,

    @ColumnInfo(name = "amount")
    var amount: String,

    @ColumnInfo(name = "order_type")
    var orderType: String
)