package com.example.cryptochallenge

import android.content.Context
import androidx.room.Room
import com.example.cryptochallenge.data.local.CryptoDatabase
import com.example.cryptochallenge.data.remote.FakeNetworkService
import com.example.cryptochallenge.utils.connectivity.FakeNetworkHelper
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class DependenciesTestRule(private val context: Context): TestRule {

    override fun apply(base: Statement, description: Description?): Statement {
        return object : Statement() {
            override fun evaluate() {
                setupDependencies()
                base.evaluate()
            }
        }

    }

    private fun setupDependencies() {
        val application = context.applicationContext as CryptoApp
        application.fakeNetworkHelper = FakeNetworkHelper()
        application.fakeDatabase = database
        application.fakeNetworkService = FakeNetworkService()
    }

    val database: CryptoDatabase by lazy {
        Room.inMemoryDatabaseBuilder(
            context.applicationContext, CryptoDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }
}