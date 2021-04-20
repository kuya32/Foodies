package com.macode.foodies.model.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fav_dishes_table")
data class FavDish(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo val image: String,
    @ColumnInfo(name = "image_source") val imageSource: String,
    @ColumnInfo val title: String,
    @ColumnInfo val type: String,
    @ColumnInfo val category: String,
    @ColumnInfo val ingredients: String,
    @ColumnInfo(name = "cooking_time") val cookingTIme: String,
    @ColumnInfo(name = "directions") val directionToCook: String,
    @ColumnInfo(name = "favorite_dish") val favoriteDish: Boolean = false
)