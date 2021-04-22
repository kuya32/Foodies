package com.macode.foodies.model.network

import com.macode.foodies.model.entities.RandomDish
import com.macode.foodies.utilities.Constants
import io.reactivex.rxjava3.core.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class RandomDishAPIService {
    private val api = Retrofit.Builder().baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .build()
        .create(RandomDishAPI::class.java)

    fun getRandomDish(): Single<RandomDish.Recipes> {
        return api.getRandomDish(Constants.API_KEY, Constants.LIMIT_LICENSE_VALUE, Constants.TAGS_VALUE, Constants.NUMBER_VALUE)
    }
}