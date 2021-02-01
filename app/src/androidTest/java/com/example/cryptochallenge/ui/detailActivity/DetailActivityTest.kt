package com.example.cryptochallenge.ui.detailActivity

import android.content.Intent
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry
import com.example.cryptochallenge.DependenciesTestRule
import com.example.cryptochallenge.R
import com.example.cryptochallenge.data.model.Order
import com.example.cryptochallenge.data.model.OrderBook
import com.example.cryptochallenge.data.model.Ticker
import com.example.cryptochallenge.data.remote.FakeNetworkServiceConfig
import com.example.cryptochallenge.ui.mainActivity.MainViewModel
import com.example.cryptochallenge.utils.OrderType
import com.example.cryptochallenge.utils.RecyclerViewMatcher
import com.example.cryptochallenge.utils.connectivity.FakeNetworkConfig
import com.example.cryptochallenge.utils.toOrderEntityList
import com.example.cryptochallenge.utils.toTickerEntity
import io.reactivex.schedulers.TestScheduler
import org.junit.Rule
import org.junit.Test

class DetailActivityTest {
    @get:Rule
    val dependencies =
        DependenciesTestRule(InstrumentationRegistry.getInstrumentation().targetContext)

    private val testScheduler: TestScheduler by lazy {
        TestScheduler()
    }

    private val ticker: Ticker by lazy {
        Ticker(book, "1", "1", "1", "1", "1", "1", "1", "2016-04-08T17:52:31.000+00:00")
    }

    private val order: Order by lazy {
        Order(book, "1", "2")
    }

    private val book = "btc_mxn"

    @Test
    fun givenNoInternetAndNoLocalData_shouldShowNoInternetMsg() {
        FakeNetworkConfig.isThereInternet = false
        FakeNetworkConfig.emitValue = false


        launch<DetailActivity>(getIntent())

        onView(withId(R.id.rvAsks))
            .check(matches(RecyclerViewMatcher.hasItemCount(0)))

        onView(withId(R.id.rvBids))
            .check(matches(RecyclerViewMatcher.hasItemCount(0)))

        onView(withId(R.id.tvLastPriceVal))
            .check(matches(withText(Ticker.defaultTicker().last)))

        onView(withText(R.string.error_on_request_data))
            .check(matches(isDisplayed()))
    }

    @Test
    fun givenNoInternetAndValidLocalData_shouldShowData() {
        FakeNetworkConfig.isThereInternet = false
        FakeNetworkConfig.emitValue = false
        setTickerInDb(ticker)
        val orderBook = OrderBook(listOf(order), listOf(order))
        setOrderBookInDb(orderBook)

        launch<DetailActivity>(getIntent())

        onView(withId(R.id.tvLastPriceVal))
            .check(matches(withText(ticker.last)))

        onView(withId(R.id.rvAsks))
            .check(matches(RecyclerViewMatcher.hasItemCount(orderBook.asks.size)))

        onView(withId(R.id.rvBids))
            .check(matches(RecyclerViewMatcher.hasItemCount(orderBook.bids.size)))

        onView(
            RecyclerViewMatcher.withRecyclerView(R.id.rvAsks)
                .atPositionOnView(orderBook.asks.lastIndex, R.id.tvPrice)
        )
            .check(matches(withText(orderBook.asks[orderBook.asks.lastIndex].price)))

        onView(
            RecyclerViewMatcher.withRecyclerView(R.id.rvBids)
                .atPositionOnView(orderBook.bids.lastIndex, R.id.tvPrice)
        )
            .check(matches(withText(orderBook.bids[orderBook.bids.lastIndex].price)))
    }

    @Test
    fun givenValidInternet_shouldShowData() {
        FakeNetworkConfig.isThereInternet = true
        FakeNetworkConfig.emitValue = true
        FakeNetworkServiceConfig.setTicker(ticker)
        val orderBook = OrderBook(listOf(order), listOf(order))
        FakeNetworkServiceConfig.setOrderBook(orderBook)

        launch<DetailActivity>(getIntent())

        onView(withId(R.id.tvLastPriceVal))
            .check(matches(withText(ticker.last)))

        onView(withId(R.id.rvAsks))
            .check(matches(RecyclerViewMatcher.hasItemCount(orderBook.asks.size)))

        onView(withId(R.id.rvBids))
            .check(matches(RecyclerViewMatcher.hasItemCount(orderBook.bids.size)))

        onView(
            RecyclerViewMatcher.withRecyclerView(R.id.rvAsks)
                .atPositionOnView(orderBook.asks.lastIndex, R.id.tvPrice)
        )
            .check(matches(withText(orderBook.asks[orderBook.asks.lastIndex].price)))

        onView(
            RecyclerViewMatcher.withRecyclerView(R.id.rvBids)
                .atPositionOnView(orderBook.bids.lastIndex, R.id.tvPrice)
        )
            .check(matches(withText(orderBook.bids[orderBook.bids.lastIndex].price)))
    }

    @Test
    fun givenValidInternet_whenOnRefreshPressed_shouldShowData() {
        FakeNetworkConfig.isThereInternet = true
        FakeNetworkConfig.emitValue = true
        FakeNetworkServiceConfig.setTicker(ticker)
        val orderBook = OrderBook(listOf(order), listOf(order))
        FakeNetworkServiceConfig.setOrderBook(orderBook)

        launch<DetailActivity>(getIntent())

        val newTicker = ticker.copy(last = "20")
        FakeNetworkServiceConfig.setTicker(newTicker)
        val newOrderBook = OrderBook(
            listOf(order.copy(price = "50"), order.copy(price = "30")),
            listOf(order.copy(price = "10"), order.copy(price = "30"))
        )
        FakeNetworkServiceConfig.setOrderBook(newOrderBook)

        onView(withId(R.id.btnReload))
            .perform(click())

        onView(withId(R.id.tvLastPriceVal))
            .check(matches(withText(newTicker.last)))

        onView(withId(R.id.rvAsks))
            .check(matches(RecyclerViewMatcher.hasItemCount(newOrderBook.asks.size)))

        onView(withId(R.id.rvBids))
            .check(matches(RecyclerViewMatcher.hasItemCount(newOrderBook.bids.size)))

        onView(
            RecyclerViewMatcher.withRecyclerView(R.id.rvAsks)
                .atPositionOnView(newOrderBook.asks.lastIndex, R.id.tvPrice)
        )
            .check(matches(withText(newOrderBook.asks[newOrderBook.asks.lastIndex].price)))

        onView(
            RecyclerViewMatcher.withRecyclerView(R.id.rvBids)
                .atPositionOnView(newOrderBook.bids.lastIndex, R.id.tvPrice)
        )
            .check(matches(withText(newOrderBook.bids[newOrderBook.bids.lastIndex].price)))
    }

    @Test
    fun givenNoInternet_whenOnReloadPressed_shouldShowNoInternetSnackBar() {
        FakeNetworkConfig.isThereInternet = true
        FakeNetworkConfig.emitValue = true
        FakeNetworkServiceConfig.setTicker(ticker)
        val orderBook = OrderBook(listOf(order), listOf(order))
        FakeNetworkServiceConfig.setOrderBook(orderBook)

        launch<DetailActivity>(getIntent())

        FakeNetworkConfig.isThereInternet = false
        FakeNetworkConfig.emitValue = true

        onView(withId(R.id.btnReload))
            .perform(click())

        onView(withText(R.string.error_no_internet))
            .check(matches(isDisplayed()))
    }

    @Test
    fun givenNoInternetAndValidInternetRestored_shouldShowData() {
        FakeNetworkConfig.isThereInternet = false
        FakeNetworkConfig.emitValue = false
        FakeNetworkServiceConfig.setTicker(ticker)
        val orderBook = OrderBook(listOf(order), listOf(order))
        FakeNetworkServiceConfig.setOrderBook(orderBook)

        launch<DetailActivity>(getIntent())

        onView(withId(R.id.rvAsks))
            .check(matches(RecyclerViewMatcher.hasItemCount(0)))

        onView(withId(R.id.rvBids))
            .check(matches(RecyclerViewMatcher.hasItemCount(0)))

        onView(withText(R.string.error_on_request_data))
            .check(matches(isDisplayed()))

        // Internet connection Restored
        FakeNetworkConfig.emitInternetConnectionActive()

        onView(withId(R.id.rvAsks))
            .check(matches(RecyclerViewMatcher.hasItemCount(orderBook.asks.size)))

        onView(withId(R.id.rvBids))
            .check(matches(RecyclerViewMatcher.hasItemCount(orderBook.bids.size)))
    }

    @Test
    fun givenValidInternetAndInternetLost_shouldShowNoInternetSnackBar(){
        FakeNetworkConfig.isThereInternet = true
        FakeNetworkConfig.emitValue = true
        FakeNetworkServiceConfig.setTicker(ticker)
        val orderBook = OrderBook(listOf(order), listOf(order))
        FakeNetworkServiceConfig.setOrderBook(orderBook)

        launch<DetailActivity>(getIntent())

        onView(withId(R.id.tvLastPriceVal))
            .check(matches(withText(ticker.last)))

        onView(withId(R.id.rvAsks))
            .check(matches(RecyclerViewMatcher.hasItemCount(orderBook.asks.size)))

        onView(withId(R.id.rvBids))
            .check(matches(RecyclerViewMatcher.hasItemCount(orderBook.bids.size)))

        // Internet connection lost
        FakeNetworkConfig.emitInternetConnectionLost()

        onView(withText(R.string.error_no_internet))
            .check(matches(isDisplayed()))
    }

    private fun setTickerInDb(ticker: Ticker) {
        dependencies.database.tickerDao().insert(ticker.toTickerEntity())
            .subscribeOn(testScheduler)
            .subscribe()

        testScheduler.triggerActions()
    }

    private fun setOrderBookInDb(orderBook: OrderBook) {
        dependencies.database.orderBookDao().insertMany(
            orderBook.bids.toOrderEntityList(OrderType.BIDS),
            orderBook.asks.toOrderEntityList(OrderType.ASKS)
        )
            .subscribeOn(testScheduler)
            .subscribe()

        testScheduler.triggerActions()
    }

    private fun getIntent(): Intent =
        Intent(
            InstrumentationRegistry.getInstrumentation().targetContext,
            DetailActivity::class.java
        )
            .apply { putExtra(MainViewModel.INTENT_BOOK, book) }
}