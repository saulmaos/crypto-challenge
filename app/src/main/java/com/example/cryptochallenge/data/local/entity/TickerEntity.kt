package com.example.cryptochallenge.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ticker")
data class TickerEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "book")
    var book: String,

    @ColumnInfo(name = "volume")
    var volume: String,

    @ColumnInfo(name = "high")
    var high: String,

    @ColumnInfo(name = "last")
    var last: String,

    @ColumnInfo(name = "low")
    var low: String,

    @ColumnInfo(name = "vwap")
    var vwap: String,

    @ColumnInfo(name = "ask")
    var ask: String,

    @ColumnInfo(name = "bid")
    var bid: String,

    @ColumnInfo(name = "created_at")
    var createdAt: String
)