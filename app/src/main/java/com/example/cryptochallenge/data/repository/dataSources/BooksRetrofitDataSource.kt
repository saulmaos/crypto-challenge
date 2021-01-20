package com.example.cryptochallenge.data.repository.dataSources

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.cryptochallenge.data.model.Book
import com.example.cryptochallenge.data.remote.NetworkService
import com.example.cryptochallenge.data.repository.NetworkResponse
import com.example.cryptochallenge.utils.CryptoIcons
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class BooksRetrofitDataSource(
    private val networkService: NetworkService
): RemoteBooksDataSource {

    private val availableBooks: MutableLiveData<NetworkResponse<List<Book>>> = MutableLiveData()

    override fun fetchAllBooks(): Single<List<Book>> {

        return networkService.doAvailableBooksCall()
            .map {
                it.payload.map { payload ->
                    Book(
                        payload.book,
                        payload.minimumAmount,
                        payload.maximumAmount,
                        payload.minimumPrice,
                        payload.maximumPrice,
                        payload.minimumValue,
                        payload.maximumValue,
                        CryptoIcons.createUrl(payload.book)
                    )
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
//        val call = networkService.doAvailableBooksCall()
//        call.enqueue(
//            object : Callback<BaseResponse<List<PayloadAvailableBookResponse>>> {
//                override fun onFailure(
//                    call: Call<BaseResponse<List<PayloadAvailableBookResponse>>>,
//                    t: Throwable
//                ) {
//                    t.printStackTrace()
//                    availableBooks.value = NetworkResponse.Error(R.string.error_on_request)
//                }
//
//                override fun onResponse(
//                    call: Call<BaseResponse<List<PayloadAvailableBookResponse>>>,
//                    response: Response<BaseResponse<List<PayloadAvailableBookResponse>>>
//                ) {
//                    if (!response.body()!!.success) {
//                        availableBooks.value = NetworkResponse.Error(R.string.error_on_request)
//                        return
//                    }
//                    availableBooks.value = NetworkResponse.Success(
//                        response.body()?.payload!!.map {
//                            Book(
//                                it.book,
//                                it.minimumAmount,
//                                it.maximumAmount,
//                                it.minimumPrice,
//                                it.maximumPrice,
//                                it.minimumValue,
//                                it.maximumValue,
//                                CryptoIcons.createUrl(it.book)
//                            )
//                        }
//                    )
//                }
//            }
//        )
    }

    override fun getAllBooks(): LiveData<NetworkResponse<List<Book>>> = availableBooks


}