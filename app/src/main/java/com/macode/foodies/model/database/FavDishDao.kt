package com.macode.foodies.model.database

import androidx.room.Dao
import androidx.room.Insert
import com.macode.foodies.model.entities.FavDish

@Dao
interface FavDishDao {

    @Insert
    suspend fun insertFavDishDetails(favDish: FavDish) {

    }
}