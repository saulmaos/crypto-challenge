package com.example.cryptochallenge.ui.mainFragment

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.swipeDown
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.example.cryptochallenge.R
import com.example.cryptochallenge.data.local.CryptoDatabase
import com.example.cryptochallenge.data.model.Book
import com.example.cryptochallenge.data.remote.FakeNetworkServiceConfig
import com.example.cryptochallenge.di.DatabaseModule
import com.example.cryptochallenge.di.NetworkModule
import com.example.cryptochallenge.di.NetworkServiceModule
import com.example.cryptochallenge.utils.*
import com.example.cryptochallenge.utils.RecyclerViewMatcher.Companion.withRecyclerView
import com.example.cryptochallenge.utils.connectivity.FakeNetworkConfig
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import io.reactivex.schedulers.TestScheduler
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@UninstallModules(DatabaseModule::class, NetworkServiceModule::class, NetworkModule::class)
@HiltAndroidTest
class MainFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var database: CryptoDatabase

    private val book: Book by lazy {
        Book(
            "btc_mxn", "1", "2", "100000", "20000", "10500", "19500", "url"
        )
    }

    private val testScheduler: TestScheduler by lazy {
        TestScheduler()
    }

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun givenNoInternetAndNoLocalData_shouldShowNoInternetMsg() {
        FakeNetworkConfig.isThereInternet = false
        FakeNetworkConfig.emitValue = false

        launchFragmentInHiltContainer<MainFragment>()

        onView(withId(R.id.rvBooks))
            .check(matches(RecyclerViewMatcher.hasItemCount(0)))
        onView(withId(R.id.tvNoInternet))
            .check(matches(isDisplayed()))
    }

    @Test
    fun givenNoInternetAndValidLocalData_shouldShowData() {
        FakeNetworkConfig.isThereInternet = false
        FakeNetworkConfig.emitValue = false
        val books = listOf(book, book.copy(book = "eth_mxn", bookPretty = "eth/mxn"))
        setBooksInDb(books)

        launchFragmentInHiltContainer<MainFragment>()

        onView(withId(R.id.rvBooks))
            .check(matches(RecyclerViewMatcher.hasItemCount(books.size)))

        onView(withRecyclerView(R.id.rvBooks).atPositionOnView(books.lastIndex, R.id.tvBook))
            .check(matches(withText(books[books.lastIndex].bookPretty)))
    }

    @Test
    fun givenValidInternet_shouldShowData() {
        FakeNetworkConfig.isThereInternet = true
        FakeNetworkConfig.emitValue = true
        val books = listOf(book, book.copy(book = "eth_mxn", bookPretty = "eth/mxn"))
        FakeNetworkServiceConfig.setBooks(books)

        launchFragmentInHiltContainer<MainFragment>()

        onView(withId(R.id.rvBooks))
            .check(matches(RecyclerViewMatcher.hasItemCount(books.size)))

        onView(withRecyclerView(R.id.rvBooks).atPositionOnView(books.lastIndex, R.id.tvBook))
            .check(matches(withText(books[books.lastIndex].bookPretty)))
    }

    @Test
    fun givenValidInternet_whenOnSwipeRefresh_shouldShowData() {
        FakeNetworkConfig.isThereInternet = true
        FakeNetworkConfig.emitValue = true
        val books = listOf(book, book.copy(book = "eth_mxn"))
        FakeNetworkServiceConfig.setBooks(books)

        launchFragmentInHiltContainer<MainFragment>()

        onView(withId(R.id.rvBooks))
            .check(matches(RecyclerViewMatcher.hasItemCount(books.size)))

        val newBooks = listOf(
            book,
            book.copy(book = "eth_mxn"),
            book.copy(book = "xrp_mxn", bookPretty = "xrp/mxn")
        )
        FakeNetworkServiceConfig.setBooks(newBooks)

        onView(withId(R.id.srlCoins))
            .perform(SwipeRefreshUtils.withCustomConstraints(swipeDown(), isDisplayingAtLeast(50)))

        onView(withId(R.id.rvBooks))
            .check(matches(RecyclerViewMatcher.hasItemCount(newBooks.size)))

        onView(withRecyclerView(R.id.rvBooks).atPositionOnView(newBooks.lastIndex, R.id.tvBook))
            .check(matches(withText(newBooks[newBooks.lastIndex].bookPretty)))
    }

    @Test
    fun givenNoInternet_whenOnSwipeRefresh_shouldShowNoInternetToast() {
        FakeNetworkConfig.isThereInternet = true
        FakeNetworkConfig.emitValue = true
        val books = listOf(book, book.copy(book = "eth_mxn"))
        FakeNetworkServiceConfig.setBooks(books)

        launchFragmentInHiltContainer<MainFragment>()

        onView(withId(R.id.rvBooks))
            .check(matches(RecyclerViewMatcher.hasItemCount(books.size)))

        FakeNetworkConfig.isThereInternet = false
        FakeNetworkConfig.emitValue = true

        onView(withId(R.id.srlCoins))
            .perform(SwipeRefreshUtils.withCustomConstraints(swipeDown(), isDisplayingAtLeast(50)))

        onView(withText(R.string.error_no_internet))
            .inRoot(ToastMatcher())
            .check(matches(isDisplayed()))
    }

    @Test
    fun givenNoInternetAndValidInternetRestored_shouldShowData() {
        FakeNetworkConfig.isThereInternet = false
        FakeNetworkConfig.emitValue = false
        val books = listOf(book, book.copy(book = "eth_mxn"))
        FakeNetworkServiceConfig.setBooks(books)

        launchFragmentInHiltContainer<MainFragment>()

        onView(withId(R.id.rvBooks))
            .check(matches(RecyclerViewMatcher.hasItemCount(0)))
        onView(withId(R.id.tvNoInternet))
            .check(matches(isDisplayed()))

        // Internet connection Restored
        FakeNetworkConfig.emitInternetConnectionActive()

        onView(withId(R.id.rvBooks))
            .check(matches(RecyclerViewMatcher.hasItemCount(books.size)))
    }

    @Test
    fun givenValidInternetAndInternetLost_shouldShowNoInternetToast() {
        FakeNetworkConfig.isThereInternet = true
        FakeNetworkConfig.emitValue = true
        val books = listOf(book, book.copy(book = "eth_mxn"))
        FakeNetworkServiceConfig.setBooks(books)

        launchFragmentInHiltContainer<MainFragment>()

        onView(withId(R.id.rvBooks))
            .check(matches(RecyclerViewMatcher.hasItemCount(books.size)))

        // Internet connection lost
        FakeNetworkConfig.emitInternetConnectionLost()

        onView(withText(R.string.error_no_internet))
            .inRoot(ToastMatcher())
            .check(matches(isDisplayed()))
    }

    private fun setBooksInDb(books: List<Book>) {
        database.bookDao().insertMany(books.toBookEntityList())
            .subscribeOn(testScheduler)
            .subscribe()

        testScheduler.triggerActions()
    }
}