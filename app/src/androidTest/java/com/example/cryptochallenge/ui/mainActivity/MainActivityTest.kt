package com.example.cryptochallenge.ui.mainActivity

import androidx.test.core.app.ActivityScenario.launch
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import com.example.cryptochallenge.R
import com.example.cryptochallenge.data.local.CryptoDatabase
import com.example.cryptochallenge.data.model.Book
import com.example.cryptochallenge.di.DatabaseModule
import com.example.cryptochallenge.di.NetworkModule
import com.example.cryptochallenge.di.NetworkServiceModule
import com.example.cryptochallenge.utils.RecyclerViewMatcher
import com.example.cryptochallenge.utils.connectivity.FakeNetworkConfig
import com.example.cryptochallenge.utils.toBookEntityList
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
class MainActivityTest {

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
    fun givenValidLocalData_whenItemViewHolderOnClick_shouldNavigateToDetailFragment() {
        FakeNetworkConfig.isThereInternet = false
        FakeNetworkConfig.emitValue = false
        val books = listOf(book, book.copy(book = "eth_mxn"))
        setBooksInDb(books)

        launch(MainActivity::class.java)

        onView(ViewMatchers.withId(R.id.rvBooks))
            .check(ViewAssertions.matches(RecyclerViewMatcher.hasItemCount(books.size)))
        onView(RecyclerViewMatcher.withRecyclerView(R.id.rvBooks).atPosition(0))
            .perform(ViewActions.click())

        onView(ViewMatchers.withId(R.id.tvLastPrice))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    private fun setBooksInDb(books: List<Book>) {
        database.bookDao().insertMany(books.toBookEntityList())
            .subscribeOn(testScheduler)
            .subscribe()

        testScheduler.triggerActions()
    }
}