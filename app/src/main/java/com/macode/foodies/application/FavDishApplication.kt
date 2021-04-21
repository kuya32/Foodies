package com.macode.foodies.application

import android.app.Application
import com.macode.foodies.model.database.FavDishRepository
import com.macode.foodies.model.database.FavDishRoomDatabase

class FavDishApplication: Application() {
    private val database by lazy { FavDishRoomDatabase.getDatabase((this@FavDishApplication)) }

    val repository by lazy { FavDishRepository(database.favDishDao()) }
}